package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.business.entity.MyPlant;
import com.temnenkov.astorobotanybot.business.script.ShakeLivesScript;
import com.temnenkov.astorobotanybot.business.script.WaterMeScript;
import com.temnenkov.astorobotanybot.business.script.WaterOthersScript;
import com.temnenkov.astorobotanybot.db.DbStore;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger("Main");

    public static void main(String[] args) {

        try {
            new Main().doWorkAndExit(args);
        } catch (InitException e) {
            logger.log(Level.SEVERE, () -> "Death on start: %s".formatted(e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Unexpected exception happens %s".formatted(e.getMessage()));
        }
    }

    private void doWorkAndExit(String[] args) {
        final FileLock preventGC = checkSecondInstance();
        logger.log(Level.FINEST, () -> "Obtain lock %s".formatted(preventGC));

        final Config config = getConfig(args);

        final DbStore<String, Serializable> database = new DbStore<>(new File(config.getConfigParameter("app.db.file")));

        initTLS(config);

        final String rootUrl = config.getConfigParameter("root.url");

        final var plant = new MyPlant(rootUrl).load();

        new WaterMeScript().invoke(plant, Integer.parseInt(config.getConfigParameter("app.water.limit")));
        new ShakeLivesScript().invoke(plant, Boolean.parseBoolean(config.getConfigParameter("app.shake.leaves")));

        new WaterOthersScript().invoke(rootUrl, Integer.parseInt(config.getConfigParameter("app.foreign.water.limit")));
        // to prevent garbage collection - still use
        logger.log(Level.FINEST, () -> "Exit, released lock %s".formatted(preventGC));
    }

    private void initTLS(@NotNull Config config) {
        final String pfxPath = config.getProperty("auth.pfx.path");
        if (pfxPath == null) {
            throw new InitException("auth.pfx.path not defined");
        }

        final File f = new File(pfxPath);
        if (!f.exists()) {
            throw new InitException("%s not exist".formatted(pfxPath));
        }
        if (f.isDirectory()) {
            throw new InitException("%s is directory".formatted(pfxPath));
        }

        final String authKeyParamName = "auth.key";
        String authKeyValue = config.getProperty(authKeyParamName);
        if (authKeyValue == null || authKeyValue.isBlank()) {
            throw new InitException("auth.key not defined");
        }

        final char[] key = authKeyValue.toCharArray();

        URL.setURLStreamHandlerFactory(new GeminiURLStreamHandlerFactory(pfxPath, key));
    }

    @NotNull
    private Config getConfig(String @NotNull [] args) {
        if (args.length < 1) {
            throw new InitException("Need properties file in argument");
        }

        final var config = new Config();
        config.load(args[0]);
        return config;
    }

    private @NotNull FileLock checkSecondInstance() {
        final String userHome = System.getProperty("user.home");
        final File file = new File(userHome, "astrobotanybot.lock");
        try {
            FileChannel fc = FileChannel.open(file.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
            FileLock lock = fc.tryLock();
            if (lock == null) {
                throw new InitException("another instance is running");
            }
            file.deleteOnExit();
            return lock;
        } catch (IOException e) {
            throw new InitException("Fail check running instances");
        }
    }

}
