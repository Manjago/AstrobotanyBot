package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.entity.DryPlants;
import com.temnenkov.astorobotanybot.business.entity.WiltingPlants;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class WaterOthersScript {
    private static final Logger logger = Logger.getLogger("WaterOthersScript");

    public void invoke(@NotNull String rootUrl, int waterLimit) {
        final var wiltingPlants = new WiltingPlants(rootUrl, waterLimit).load();
        if (wiltingPlants.doWater()) {
            logger.log(Level.INFO, "Foreign wilting plant watered");
            return;
        }

        final var dryPlants = new DryPlants(rootUrl, waterLimit).load();
        if (dryPlants.doWater()) {
            logger.log(Level.INFO, "Foreign dry plant watered");
            return;
        }

        logger.log(Level.INFO, "No foreign plants watered");
    }
}
