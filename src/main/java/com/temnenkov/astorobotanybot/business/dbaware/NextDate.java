package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;

@RequiredArgsConstructor
public class NextDate {

    private final DbStore<String, Serializable> database;
    private final String key;

    public void storeNext(@NotNull Instant instant) {
        database.put(key, instant);
    }

    @Nullable
    public Instant loadNext() {
        return (Instant) database.get(key);
    }

    public AllowedResult allowed() {
        final Instant now = Instant.now();
        final Instant nextDate = loadNext();
        if (nextDate == null) {
            return new AllowedResult(now, null, true);
        } else {
            return new AllowedResult(now, nextDate, now.isAfter(nextDate));
        }
    }

    public record AllowedResult(Instant now, Instant nextDate, boolean passed) {
    }

}
