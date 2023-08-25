package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;

@RequiredArgsConstructor
public class NextForeignWatering {

    private static final String KEY = "next.foreign.watering.timestamp";
    private final DbStore<String, Serializable> database;

    public void storeNext(@NotNull Instant instant) {
        database.put(KEY, instant);
    }

    @Nullable
    public Instant loadNext() {
        return (Instant) database.get(KEY);
    }

}
