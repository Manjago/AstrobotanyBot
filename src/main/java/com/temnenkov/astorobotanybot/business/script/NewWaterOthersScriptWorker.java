package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewWaterOthersScriptWorker {

    private static final Logger logger = Logger.getLogger("NewWaterOthersScriptWorker");
    private final GameClient gameClient;
    private final PlantParser plantParser;
    private final GardenParser gardenParser;

    private record IdToStage(String id, PlantStage plantStage) {
    }
    
    @NotNull NewWaterOtherScriptResult processGarden(@NotNull GardenPageState gardenPageState) {

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No pretenders for %s".formatted(gardenPageState));
            return NewWaterOtherScriptResult.NoPretenders.INSTANCE;
        }

        final Map<String, String> toWater = loadPlantsToWater(gardenPageState);

        final List<IdToStage> pretenders = toWater.entrySet().stream()
                .map(s -> new IdToStage(s.getKey(), PlantStage.extractFromString(s.getValue())))
                .sorted(Comparator.comparingInt(o -> o.plantStage().getWateringPriority()))
                .toList();

        return Objects.requireNonNullElse(waterByPriority(pretenders), NewWaterOtherScriptResult.NoPretenders.INSTANCE);
    }

    @Nullable
    NewWaterOtherScriptResult waterByPriority(@NotNull List<IdToStage> pretenders) {
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
                return new NewWaterOtherScriptResult.TooEarly(stateBefore, stateAfter);
            } else {
                return new NewWaterOtherScriptResult.Watered(stateBefore.water(), stateAfter.water());
            }
        }
        return null;
    }

    @NotNull
    Map<String, String> loadPlantsToWater(@NotNull GardenPageState gardenPageState) {
        final Map<String, String> toWater = new HashMap<>(gardenPageState.idToStatus());
        GardenPageState currentPage = gardenPageState;
        logPage(currentPage);
        while (currentPage.nextPage() != null) {
            currentPage = gardenParser.parse(gameClient.justLoad(currentPage.nextPage()));
            logPage(currentPage);
            toWater.putAll(currentPage.idToStatus());
        }
        return toWater;
    }

    private static void logPage(@NotNull GardenPageState currentPage) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Load page %s".formatted(currentPage));
        }
    }

}
