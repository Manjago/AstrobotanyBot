package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.LogUtils;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewWaterOthersScript {
    private static final Logger logger = Logger.getLogger("NewWaterOthersScript");
    private final GameClient gameClient;
    private final NewWaterOthersScriptWorker newWaterOthersScriptWorker;
    private final GardenParser gardenParser;

    public @NotNull NewWaterOtherScriptResult invoke() {
        final var gardenPageState = gardenParser.parse(gameClient.wiltingPlants());

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No wilting");
            return newWaterOthersScriptWorker.processGarden(gardenParser.parse(gameClient.dryPlants()), this::loadPlantsToWater);
        } else {
            logger.log(Level.FINE, () -> "Wilting found: %d".formatted(gardenPageState.idToStatus().size()));
            return newWaterOthersScriptWorker.processGarden(gardenPageState, this::loadPlantsToWater);
        }
    }

    @NotNull
    Map<String, String> loadPlantsToWater(@NotNull GardenPageState gardenPageState) {
        final Map<String, String> toWater = new HashMap<>(gardenPageState.idToStatus());
        GardenPageState currentPage = gardenPageState;
        LogUtils.logFine(logger, currentPage);
        while (currentPage.nextPage() != null) {
            currentPage = gardenParser.parse(gameClient.justLoad(currentPage.nextPage()));
            LogUtils.logFine(logger, currentPage);
            toWater.putAll(currentPage.idToStatus());
        }
        return toWater;
    }


}
