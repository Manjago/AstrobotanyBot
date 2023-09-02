package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeenTracker {
    private static final Logger logger = Logger.getLogger("SeenTracker");
    private final DbStore<String, Serializable> database;
    private final String key;

    public SeenTracker(DbStore<String, Serializable> database, String key) {
        this.database = database;
        this.key = key;
    }

    public void refresh() {
        // temp
        database.remove("seen.tracker." + key);
        database.remove(key);
        logger.log(Level.INFO, "REFRESHED");
    }

    public boolean notSeen(@NotNull String s) {
        final Set<String> set = getSet();
        boolean result = !set.contains(s);
        if (result) {
            logger.info(() -> "%s not seen".formatted(s));
        } else {
            logger.info(() -> "%s ALREADY SEEN".formatted(s));
        }
        return result;
    }

    public void markAsSeen(@NotNull String s) {
        final HashSet<String> set = getSet();
        set.add(s);
        logger.info(() -> "%s mark as seen".formatted(s));
        database.put(key, set);
    }

    @NotNull
    private HashSet<String> getSet() {
        @SuppressWarnings("unchecked") final HashSet<String> set = (HashSet<String>) database.get(key);
        if (set != null) {
            return set;
        }
        return new HashSet<>();
    }


}
