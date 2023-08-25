package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.entity.Plant;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class ShakeLivesScript {
    private static final Logger logger = Logger.getLogger("ShakeLivesScript");
    public static void invoke(@NotNull Plant plant, boolean needShakeLives) {
        if (needShakeLives) {
            plant.doSnakeLeaves();
            logger.log(Level.INFO, "shake lives enabled - do work");
        } else {
            logger.log(Level.INFO, "shake lives disabled - do nothing");
        }
    }
}
