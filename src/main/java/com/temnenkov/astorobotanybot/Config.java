package com.temnenkov.astorobotanybot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static final Logger logger = Logger.getLogger(Config.class.getName());
    private final Properties properties = new Properties();


    void load(@NotNull String path) {
        logger.log(Level.FINE, () -> "Loading properties from %s".formatted(path));

        try (final var is = new FileInputStream(path)) {
            properties.load(is);
        } catch (Exception e) {
            throw new InitException("Fail loading properties from %s".formatted(path));
        }
    }

    @NotNull
    String getConfigParameter(@NotNull String key) {
        final String value = getProperty(key);
        if (value == null || value.isBlank()) {
            throw new InitException("%s not defined".formatted(key));
        }
        return value;
    }

    @Nullable
    String getProperty(@NotNull String key) {
        return properties.getProperty(key);
    }
}
