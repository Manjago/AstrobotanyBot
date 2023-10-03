package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class WaterOthersScript {
    private static final Logger logger = Logger.getLogger("WaterOthersScript");
    private final GameClient gameClient;
    private final WaterOthersScriptWorker waterOthersScriptWorker;
    private final GardenParser gardenParser;
    private final GardenCollector gardenCollector;

    public @NotNull WaterOtherScriptResult invoke() {
        final var gardenPageState = gardenParser.parse(gameClient.wiltingPlants());

        if (gardenPageState.idToStatus().isEmpty()) {
            logger.log(Level.FINE, () -> "No wilting");
            return waterOthersScriptWorker.processGarden(gardenParser.parse(gameClient.dryPlants()), gardenCollector::collectIdToStatus);
        } else {
            logger.log(Level.FINE, () -> "Wilting found: %d".formatted(gardenPageState.idToStatus().size()));
            return waterOthersScriptWorker.processGarden(gardenPageState, gardenCollector::collectIdToStatus);
        }
    }

}
