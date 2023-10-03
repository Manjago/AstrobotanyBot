package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class WaterOthersScriptWorker {

    private static final Logger logger = Logger.getLogger("WaterOthersScriptWorker");
    private final GameClient gameClient;
    private final PlantParser plantParser;

    // pakage visibility for unit test
    record IdToStage(String id, PlantStage plantStage) {
    }
    
    @NotNull WaterOtherScriptResult processGarden(@NotNull GardenPageState gardenPageState,
                                                  @NotNull Function<GardenPageState, Map<String, String>> traverseGarden) {
        return processGarden(gardenPageState, traverseGarden, this::waterByPriority);
    }

    @NotNull WaterOtherScriptResult processGarden(@NotNull GardenPageState gardenPageState,
                                                  @NotNull Function<GardenPageState, Map<String, String>> traverseGarden,
                                                  @NotNull Function<List<IdToStage>, WaterOtherScriptResult> waterByPriority
                                                     ) {

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No pretenders for %s".formatted(gardenPageState));
            return WaterOtherScriptResult.NoPretenders.INSTANCE;
        }

        final Map<String, String> toWater = traverseGarden.apply(gardenPageState);

        final List<IdToStage> pretenders = toWater.entrySet().stream()
                .map(s -> new IdToStage(s.getKey(), PlantStage.extractFromString(s.getValue())))
                .sorted(Comparator.comparingInt(o -> o.plantStage().getWateringPriority()))
                .toList();

        return waterByPriority.apply(pretenders);
    }

    @Nullable
    WaterOtherScriptResult waterByPriority(@NotNull List<IdToStage> pretenders) {
        for (IdToStage pretender : pretenders) {
            final var stateBefore = plantParser.parse(gameClient.plant(pretender.id()));
            logger.log(Level.FINE, () -> "For %s state before %s".formatted(pretender.id(), stateBefore));
            if (stateBefore.hasFence() || (stateBefore.water() == 100)) {
                continue;
            }
            gameClient.waterPlant(pretender.id);
            final var stateAfter = plantParser.parse(gameClient.plant(pretender.id()));
            logger.log(Level.FINE, () -> "For %s state after %s".formatted(pretender.id(), stateBefore));
            if (stateBefore.water() == stateAfter.water()) {
                return new WaterOtherScriptResult.TooEarly(stateBefore, stateAfter);
            } else {
                return new WaterOtherScriptResult.Watered(stateBefore.water(), stateAfter.water());
            }
        }
        return WaterOtherScriptResult.NoPretenders.INSTANCE;
    }

}