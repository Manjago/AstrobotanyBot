package com.temnenkov.astorobotanybot.business.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseUtulsTest {

    @Test
    void removePrevix() {
        assertEquals("123", ParseUtuls.removePrevix("rt123", "rt"));
    }

    @Test
    void saveAtter() {
        assertEquals("15 [38;5;197mred[0m petals.", ParseUtuls.saveAfter("=> /app/pond/tribute/red Toss in 15 [38;5;197mred[0m petals.",
                "Toss in "));
    }

    @Test
    void saveBefore() {
        assertEquals("15", ParseUtuls.saveBefore("15 [38;5;197mred[0m petals.",
                " "));
    }

    @Test
    void saveAfterNoContains() {
        assertEquals("123", ParseUtuls.saveAfter("123", "456"));
    }

    @Test
    void saveBeforeNoContains() {
        assertEquals("123", ParseUtuls.saveBefore("123", "456"));
    }

    @Test
    void mid() {
        assertEquals("15", ParseUtuls.mid("=> /app/pond/tribute/red Toss in 15 [38;5;197mred[0m petals.",
                "Toss in ", " "));
    }
}