package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewWaterOthersScript {
    private static final Logger logger = Logger.getLogger("NewWaterOthersScript");
    private final GameClient gameClient;
    private final PlantParser plantParser;
    private final GardenParser gardenParser;

    public NewWaterOtherScriptResult invoke() {
        final var gardenPageState = gardenParser.parse(gameClient.wiltingPlants());

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No wilting");
            return processGarden(gardenParser.parse(gameClient.dryPlants()));
        } else {
            logger.log(Level.FINE, () -> "Wilting found: %d".formatted(gardenPageState.idToStatus().size()));
            return processGarden(gardenPageState);
        }
    }

    private NewWaterOtherScriptResult processGarden(@NotNull GardenPageState gardenPageState) {

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No pretenders for %s".formatted(gardenPageState));
            return NewWaterOtherScriptResult.NoPretenders.INSTANCE;
        }

        //todo log
        final Map<String, String> toWater = new HashMap<>(gardenPageState.idToStatus());
        GardenPageState currentPage = gardenPageState;
        while (currentPage.nextPage() != null) {
            currentPage = gardenParser.parse(gameClient.justLoad(currentPage.nextPage()));
            toWater.putAll(currentPage.idToStatus());
        }

        record IdToStage(String id, PlantStage plantStage) {
        }

        final List<IdToStage> pretenders = toWater.entrySet().stream()
                .map(s -> new IdToStage(s.getKey(), PlantStage.extractFromString(s.getValue())))
                .sorted(Comparator.comparingInt(o -> o.plantStage().getWateringPriority()))
                .toList();

        for (IdToStage pretender : pretenders) {
            final var stateBefore = plantParser.parse(gameClient.plant(pretender.id()));
            if (stateBefore.water() == 100) {
                continue;
            }
            gameClient.waterPlant(pretender.id);
            final var stateAfter = plantParser.parse(gameClient.plant(pretender.id()));
            if (stateBefore.water() == stateAfter.water()) {
                return new NewWaterOtherScriptResult.TooEarly(stateBefore, stateAfter);
            } else {
                return new NewWaterOtherScriptResult.Watered(stateBefore.water(), stateAfter.water());
            }
        }

        return NewWaterOtherScriptResult.NoPretenders.INSTANCE;
    }

}
