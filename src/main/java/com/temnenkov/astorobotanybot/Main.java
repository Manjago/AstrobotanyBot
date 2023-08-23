package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.business.entity.MyPlant;
import com.temnenkov.astorobotanybot.business.script.ShakeLivesScript;
import com.temnenkov.astorobotanybot.business.script.WaterScript;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            final Properties config = getConfig(args);

            initTLS(config);

            final String rootUrl = getConfigParameter(config, "root.url", "root.url not defined");

            final String waterLimitString = getConfigParameter(config, "app.water.limit", "app.water.limit not defined");

            final String appShakeLives = getConfigParameter(config, "app.shake.leaves", "app.shake.leaves not defined");

            final var plant = new MyPlant(rootUrl).load();

            WaterScript.invoke(plant, Integer.parseInt(waterLimitString));
            ShakeLivesScript.invoke(plant, Boolean.parseBoolean(appShakeLives));
        } catch (InitException e) {
            logger.log(Level.SEVERE, () -> "Death on start: %s".formatted(e.getMessage()));
        }  catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Unexpected exception happens %s".formatted(e.getMessage()));
        }
    }

    @NotNull
    private static String getConfigParameter(@NotNull Properties config, @NotNull String key, @NotNull String message) {
        final String rootUrl = config.getProperty(key);
        if (rootUrl == null || rootUrl.isBlank()) {
            throw new InitException(message);
        }
        return rootUrl;
    }

    private static void initTLS(@NotNull Properties config) {
        final String pfxPath = config.getProperty("auth.pfx.path");
        if (pfxPath == null) {
            throw new InitException("auth.pfx.path not defined");
        }

        final File f = new File(pfxPath);
        if(!f.exists()) {
            throw new InitException("%s not exist".formatted(pfxPath));
        }
        if(f.isDirectory()) {
            throw new InitException("%s is directory".formatted(pfxPath));
        }

        final String authKey = "auth.key";
        if (config.getProperty(authKey) == null || config.getProperty(authKey).isBlank()) {
            throw new InitException( "auth.key not defined");
        }

        final char[] key = config.getProperty(authKey).toCharArray();

        URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory(pfxPath, key));
    }

    @NotNull
    private static Properties getConfig(@NotNull String [] args) {
        if (args.length < 1) {
            throw new InitException("Need properties file in argument");
        }
        if (logger.isLoggable(Level.FINE)) {
            throw new InitException( "Loading properties from %s".formatted(args[0]));
        }

        final var config = new Properties();
        try (final var is = new FileInputStream(args[0])) {
            config.load(is);
        } catch (Exception e) {
            throw new InitException( "Fail loading properties from %s".formatted(args[0]));
        }
        return config;
    }

    private static class InitException extends RuntimeException {
        public InitException(String message) {
            super(message);
        }
    }
}
