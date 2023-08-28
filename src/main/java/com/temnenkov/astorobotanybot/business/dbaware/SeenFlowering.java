package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.business.entity.FloweringInfo;
import com.temnenkov.astorobotanybot.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class SeenFlowering {
    private final String prefix;
    private final DbStore<String, Serializable> database;

    public void cleanTTL() {
        final Instant now = Instant.now();
        final Stream<String> keys = database.keys(s -> s.startsWith(prefix));
        keys.forEach(key -> {
            final FloweringInfo info = (FloweringInfo) database.get(key);
            if (info != null && info.ttl().isBefore(now)) {
                database.remove(key);
            }
        });
    }

    public boolean seen(@NotNull String url, int rate) {
        final String key = key(url, rate);
        return database.get(key) != null;
    }

    @Contract(pure = true)
    private @NotNull String key(@NotNull String url, int rate) {
        return prefix + "." + url + "." + rate;
    }

}
