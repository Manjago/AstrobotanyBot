package com.temnenkov.astorobotanybot.db;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DbStore<K, V> {
    private final File log;
    private final Map<K, V> map = new HashMap<>();
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Object lock = new Object();

    public DbStore(@NotNull File log) {
        this.log = log;

        try (Stream<String> stream = Files.lines(log.toPath())) {
            stream.forEach(s -> {
                final String[] cmdKeyValue = s.split(" ");
                switch (cmdKeyValue[0]) {
                    case "PUT" -> {
                        final K key = deserialize(cmdKeyValue[1]);
                        final V value = deserialize(cmdKeyValue[2]);
                        map.put(key, value);
                    }
                    case "REM" -> {
                        final K key = deserialize(cmdKeyValue[1]);
                        map.remove(key);
                    }
                }
            });
        } catch (IOException e) {
            throw new DbPanicException(e);
        }
    }

    public void put(@NotNull K key, @NotNull V value) {
        synchronized (lock) {
            final String serializedKey = serialize(key);
            final String serializedValue = serialize(value);
            final String dbString = "PUT " + serializedKey + " " + serializedValue + "\n";
            appendLine(dbString);
            map.put(key, value);
        }
    }

    @Nullable
    public V get(@NotNull K key) {
        return map.get(key);
    }

    public void remove(@NotNull K key) {
        synchronized (lock) {
            final String serializedKey = serialize(key);
            final String dbString = "REM " + serializedKey + "\n";
            appendLine(dbString);
            map.remove(key);
        }
    }

    private void appendLine(@NotNull String dbString) {
        try {
            Files.write(log.toPath(), dbString.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new DbPanicException(e);
        }
    }

    @NotNull
    private <T> T deserialize(@NotNull String serialized) {
        final byte[] decoded = decoder.decode(serialized);
        try (ByteArrayInputStream b = new ByteArrayInputStream(decoded)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                //noinspection unchecked
                return (T) o.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new DbPanicException(e);
        }
    }

    @NotNull
    private <T> String serialize(@NotNull T object) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(object);
            }
            return encoder.encodeToString(b.toByteArray());
        } catch (IOException e) {
            throw new DbPanicException(e);
        }
    }

    public static class DbPanicException extends RuntimeException {
        public DbPanicException(Throwable cause) {
            super(cause);
        }
    }
}
