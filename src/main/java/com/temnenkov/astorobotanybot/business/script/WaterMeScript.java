package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.entity.Plant;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class WaterMeScript {
    private static final Logger logger = Logger.getLogger(WaterMeScript.class.getName());
    public static void invoke(@NotNull Plant plant, int waterLimit) {
        final int waterQty = plant.waterQty();
        if (waterQty < waterLimit) {
            plant.doWater();
            logger.log(Level.INFO, () -> "Water %d, waterLimit %d: do water".formatted(waterQty, waterLimit));
        } else {
            logger.log(Level.INFO, () -> "Water %d, waterLimit %d: no water".formatted(waterQty, waterLimit));
        }
    }
}
