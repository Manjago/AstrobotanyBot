package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.dbaware.NextForeignWatering;
import com.temnenkov.astorobotanybot.business.entity.DryPlants;
import com.temnenkov.astorobotanybot.business.entity.WiltingPlants;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class WaterOthersScript {
    private static final Logger logger = Logger.getLogger("WaterOthersScript");
    private final NextForeignWatering nextForeignWatering;

    public void invoke(@NotNull String rootUrl, int waterLimit) {

        final Instant now = Instant.now();
        final Instant nextWatering = nextForeignWatering.loadNext();
        if (nextWatering != null) {
            if (now.isBefore(nextWatering)) {
                logger.log(Level.FINEST, () -> "Now " + now + ", nextTime " + nextWatering + " no foreign watering");
            }
        }
        logger.log(Level.INFO, () -> "Now " + now + ", nextTime " + nextWatering + " do foreign watering");

        final var wiltingPlants = new WiltingPlants(rootUrl, waterLimit).load();

        switch (wiltingPlants.doWater()) {
            case WATERED -> {
                logger.log(Level.INFO, "Foreign wilting plant watered");
                nextForeignWatering.storeNext(Instant.now().plus(30, ChronoUnit.MINUTES));
                return;
            }
            case TOO_EARLY -> {
                logger.log(Level.INFO, "To early for wilting water");
                nextForeignWatering.storeNext(Instant.now().plus(5, ChronoUnit.MINUTES));
                return;
            }
        }

        final var dryPlants = new DryPlants(rootUrl, waterLimit).load();

        switch (dryPlants.doWater()) {
            case WATERED -> {
                logger.log(Level.INFO, "Foreign dry plant watered");
                nextForeignWatering.storeNext(Instant.now().plus(30, ChronoUnit.MINUTES));
                return;
            }
            case TOO_EARLY -> {
                logger.log(Level.INFO, "To early for dry water");
                nextForeignWatering.storeNext(Instant.now().plus(5, ChronoUnit.MINUTES));
                return;
            }
        }

        logger.log(Level.INFO, "No foreign plants watered");
        nextForeignWatering.storeNext(Instant.now().plus(5, ChronoUnit.MINUTES));
    }
}
