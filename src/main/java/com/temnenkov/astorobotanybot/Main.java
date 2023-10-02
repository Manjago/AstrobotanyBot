package com.temnenkov.astorobotanybot;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.business.dbaware.NextCompress;
import com.temnenkov.astorobotanybot.business.dbaware.SeenTracker;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.PondParser;
import com.temnenkov.astorobotanybot.business.parser.dto.PetailColor;
import com.temnenkov.astorobotanybot.business.script.GardenCollector;
import com.temnenkov.astorobotanybot.business.script.NewPickPetailsScript;
import com.temnenkov.astorobotanybot.business.script.NewPondScript;
import com.temnenkov.astorobotanybot.business.script.NewShakeLivesScript;
import com.temnenkov.astorobotanybot.business.script.NewShakeLivesScriptResult;
import com.temnenkov.astorobotanybot.business.script.NewWaterMeScript;
import com.temnenkov.astorobotanybot.business.script.NewWaterMeScriptResult;
import com.temnenkov.astorobotanybot.business.script.NewWaterOtherScriptResult;
import com.temnenkov.astorobotanybot.business.script.NewWaterOthersScript;
import com.temnenkov.astorobotanybot.business.script.NewWaterOthersScriptWorker;
import com.temnenkov.astorobotanybot.db.DbStore;
import com.temnenkov.astorobotanybot.protocol.GeminiURLStreamHandlerFactory;
import com.temnenkov.astorobotanybot.utils.DbTimer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    // todo remove ALL old code and rename others
    private static void newWork(DbStore<String, Serializable> database, String rootUrl, GeminiHelper geminiHelper,
                                @NotNull Config config) {
        final var gameClient = new GameClient(rootUrl, geminiHelper);
        final var plantParser = new PlantParser();
        final var gardenParser = new GardenParser();
        final var newWaterOthersScriptWorker = new NewWaterOthersScriptWorker(gameClient, plantParser);
        final var gardenCollector = new GardenCollector(gameClient, gardenParser);
        final var newWaterOthersScript = new NewWaterOthersScript(gameClient, newWaterOthersScriptWorker,
                gardenParser, gardenCollector);
        final var newShakeLivesScript = new NewShakeLivesScript(gameClient, plantParser);
        final var newWaterMeScript = new NewWaterMeScript(gameClient, plantParser,
                Integer.parseInt(config.getConfigParameter("app.water.limit")));
        final var pondParser = new PondParser();
        final var newPondScript = new NewPondScript(gameClient, pondParser);
        final var seenTracker = new SeenTracker(database, "pick.petail");
        final var newPickPetailsScript = new NewPickPetailsScript(gameClient, gardenParser, gardenCollector,
                seenTracker);

        new DbTimer<NewWaterMeScriptResult>(database, "new.water.script").fire(Instant.now(),
                newWaterMeScript::invoke, (r, f) -> Instant.now().plus(60, ChronoUnit.MINUTES),
                (t, f) -> Instant.now().plus(10, ChronoUnit.MINUTES));

        new DbTimer<NewShakeLivesScriptResult>(database, "new.shake.script").fire(Instant.now(),
                newShakeLivesScript::invoke, (r, f) -> Instant.now().plus(20, ChronoUnit.MINUTES),
                (t, f) -> Instant.now().plus(5, ChronoUnit.MINUTES));

        new DbTimer<NewWaterOtherScriptResult>(database, "new.water.others.script").fire(Instant.now(),
                newWaterOthersScript::invoke, (r, f) -> Instant.now().plus(31, ChronoUnit.MINUTES),
                (t, f) -> Instant.now().plus(5, ChronoUnit.MINUTES));

        final PetailColor blessedColor = newPondScript.invoke();
        final PetailColor prevBlessedColor = (PetailColor) database.get("blessedColor");
        if (!blessedColor.equals(prevBlessedColor)) {
            database.put("blessedColor", blessedColor);
            seenTracker.refresh();
        }
        newPickPetailsScript.invoke();
    }

    private void doWorkAndExit(String[] args) {
        logger.log(Level.INFO, "--- 1.1.0-SNAPSHOT ---");
        final FileLock preventGC = checkSecondInstance();
        logger.log(Level.INFO, () -> "Obtain lock %s".formatted(preventGC));

        final Config config = getConfig(args);

        final DbStore<String, Serializable> database =
                new DbStore<>(new File(config.getConfigParameter("app.db.file")));
        final var nextCompress = new NextCompress(database);
        final var allowed = nextCompress.allowed();
        logger.log(Level.INFO, () -> "Check timer for compress: %s".formatted(allowed));
        if (allowed.passed()) {
            database.compress();
            nextCompress.storeNext(Instant.now().plus(1, ChronoUnit.DAYS));
        }

        initTLS(config);
        final GeminiHelper geminiHelper = new GeminiHelper();

        final String rootUrl = config.getConfigParameter("root.url");

        //mainWork(database, rootUrl, geminiHelper, config);
        newWork(database, rootUrl, geminiHelper, config);

        // to prevent garbage collection - still use
        logger.log(Level.INFO, () -> "Exit, released lock %s".formatted(preventGC));
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
            FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
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
