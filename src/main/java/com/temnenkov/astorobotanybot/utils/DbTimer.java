package com.temnenkov.astorobotanybot.utils;

import com.temnenkov.astorobotanybot.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class DbTimer<T> {

    private static final Logger logger = Logger.getLogger(DbTimer.class.getName());
    private final DbStore<String, Serializable> dbStore;
    private final String key;


    public Result<T> fire(@NotNull Instant now, @NotNull Callable<T> callable, @NotNull BiFunction<T, Instant, Instant> onSuccess, @NotNull BiFunction<Throwable, Instant, Instant> onError) {
        final Instant fireTime = (Instant) dbStore.get(key);
        if (fireTime != null && fireTime.isAfter(now)) {
            logger.log(Level.FINE, () -> "Now=%s, fireTime=%s do nothing".formatted(now, fireTime));
            return null;
        }

        Result<T> result = Result.runCatching(callable).onSuccess(
                v -> {
                    final Instant nextFire = onSuccess.apply(v, fireTime);
                    if (nextFire != null) {
                        logger.log(Level.FINE, () -> "Store key='%s', value='%s'".formatted(key, nextFire));
                        dbStore.put(key, nextFire);
                    }
                }
        ).onFailure(t -> {
            logger.log(Level.SEVERE, "Fail process timer %s".formatted(key), t);
            final Instant nextFire = onError.apply(t, fireTime);
            if (nextFire != null) {
                logger.log(Level.FINE, () -> "Store key='%s', value='%s'".formatted(key, nextFire));
                dbStore.put(key, nextFire);
            }
        });

        logger.log(Level.FINE, () -> "Result = %s".formatted(result));
        return result;
    }

}
