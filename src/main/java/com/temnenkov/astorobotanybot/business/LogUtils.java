package com.temnenkov.astorobotanybot.business;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class LogUtils {
    public static void logFine(@NotNull Logger logger, @NotNull Object loggedObject) {
        logger.log(Level.FINE, () -> "Load page %s".formatted(loggedObject));
    }
}
