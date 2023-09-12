package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
            return newWaterOthersScriptWorker.processGarden(gardenParser.parse(gameClient.dryPlants()));
        } else {
            logger.log(Level.FINE, () -> "Wilting found: %d".formatted(gardenPageState.idToStatus().size()));
            return newWaterOthersScriptWorker.processGarden(gardenPageState);
        }
    }
}
