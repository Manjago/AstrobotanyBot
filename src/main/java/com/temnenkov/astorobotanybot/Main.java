package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.business.MyPlant;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;

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
            if (args.length < 1) {
                logger.severe("Need properties file in argument");
                return;
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Loading properties from %s".formatted(args[0]));
            }

            final var config = new Properties();
            try (final var is = new FileInputStream(args[0])) {
                config.load(is);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Fail loading properties from %s".formatted(args[0]), e);
                return;
            }

            final String pfxPath = config.getProperty("auth.pfx.path");
            if (pfxPath == null) {
                logger.log(Level.SEVERE, "auth.pfx.path not defined");
                return;
            }

            final File f = new File(pfxPath);
            if(!f.exists()) {
                logger.log(Level.SEVERE, "%s not exist".formatted(pfxPath));
                return;
            }
            if(f.isDirectory()) {
                logger.log(Level.SEVERE, "%s is directory".formatted(pfxPath));
                return;
            }

            if (config.getProperty("auth.key") == null || config.getProperty("auth.key").isBlank()) {
                logger.log(Level.SEVERE, "auth.key not defined");
                return;
            }

            final char[] key = config.getProperty("auth.key").toCharArray();

            URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory(pfxPath, key));

            final String rootUrl = config.getProperty("root.url");
            if (rootUrl == null || rootUrl.isBlank()) {
                logger.log(Level.SEVERE, "root.url not defined");
                return;
            }

            final String waterLimitString = config.getProperty("app.water.limit");
            if (waterLimitString == null || waterLimitString.isBlank()) {
                logger.log(Level.SEVERE, "app.water.limit not defined");
                return;
            }

            doWork(rootUrl, Integer.parseInt(waterLimitString));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception %s".formatted(e.getMessage()), e);
        }
    }

    private static void doWork(String rootUrl, int waterLimit) {
        final var plant = new MyPlant(rootUrl).load();
        final int waterQty = plant.waterQty();
        if (waterQty < waterLimit) {
            plant.doWater();
        }
        plant.doSnakeLeaves();
    }
}
