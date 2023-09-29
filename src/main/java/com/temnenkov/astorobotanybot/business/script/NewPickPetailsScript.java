package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.dbaware.SeenTracker;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewPickPetailsScript {
    private static final Logger logger = Logger.getLogger("NewPickPetailsScript");
    private final GameClient gameClient;
    private final GardenParser gardenParser;
    private final GardenCollector gardenCollector;
    private final SeenTracker seenTracker;

    public void invoke() {
        final var gardenPageState = gardenParser.parse(gameClient.wiltingPlants());
        final Map<String, String> idToStatus = gardenCollector.collectIdToStatus(gardenPageState);
        final AtomicInteger processed = new AtomicInteger();
        idToStatus.keySet().stream()
                .filter(seenTracker::notSeen)
                .peek(seenTracker::markAsSeen)
                .peek(s -> processed.getAndIncrement())
                .forEach(gameClient::pickPetail);
        logger.log(Level.INFO, () -> "Processed %d items".formatted(processed.get()));
    }
}
