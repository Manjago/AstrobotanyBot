package com.temnenkov.astorobotanybot.db;


import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DbStoreTest {
    @Test
    void smoke() {
        final File log = new File("target/test.txt");
        if (log.exists()) {
            assertTrue(log.delete());
        }
        var dbStore = new DbStore<>(log);
        assertNull(dbStore.get("1"));
        dbStore.put("1", "2");
        assertEquals("2", dbStore.get("1"));
        dbStore.put("1", "3");
        assertEquals("3", dbStore.get("1"));
        dbStore.remove("1");
        assertNull(dbStore.get("1"));
        dbStore.put("2", "333");
        assertNull(dbStore.get("1"));
        dbStore.put("1", "5");
        assertEquals("5", dbStore.get("1"));
        assertEquals("333", dbStore.get("2"));

        dbStore = new DbStore<>(log);
        assertEquals("5", dbStore.get("1"));
        assertEquals("333", dbStore.get("2"));
    }
}