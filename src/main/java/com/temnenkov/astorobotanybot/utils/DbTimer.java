package com.temnenkov.astorobotanybot.utils;

import com.temnenkov.astorobotanybot.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class DbTimer<T> {

    private static final Logger logger = Logger.getLogger(DbTimer.class.getName());
    private final DbStore<String, Serializable> dbStore;
    private final String key;

    @Nullable public Result<T> tryFire(@NotNull Instant now, @NotNull Supplier<T> r, @NotNull BiFunction<T, Instant, Instant> onSuccess, @NotNull BiFunction<Throwable, Instant, Instant> onError) {
        Instant fireTime = null;
        try {
            fireTime = (Instant) dbStore.get(key);
            if (fireTime != null && fireTime.isAfter(now)) {
                final var loggedFireTime = fireTime;
                logger.log(Level.FINE,() -> "Now=%s, fireTime=%s do nothing".formatted(now, loggedFireTime));
                return null;
            }

            final T value = r.get();
            final Instant nextFire = onSuccess.apply(value, fireTime);
            if (nextFire != null) {
                logger.log(Level.FINE, () -> "Store key='%s', value='%s'".formatted(key, nextFire));
                dbStore.put(key, nextFire);
            }
            final Result<T> result = Result.success(value);
            logger.log(Level.FINE, () -> "Result = %s".formatted(result));
            return result;

        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Fail process timer %s".formatted(key), t);
            final Instant nextFire = onError.apply(t, fireTime);
            if (nextFire != null) {
                logger.log(Level.FINE, () -> "Store key='%s', value='%s'".formatted(key, nextFire));
                dbStore.put(key, nextFire);
            }
            final Result<T> failure = Result.failure(t);
            logger.log(Level.FINE, () -> "Result = %s".formatted(failure));
            return failure;
        }
    }
}
