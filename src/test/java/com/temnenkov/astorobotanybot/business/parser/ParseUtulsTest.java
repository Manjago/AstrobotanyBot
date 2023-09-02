package com.temnenkov.astorobotanybot.business.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseUtulsTest {

    @Test
    void removePrevix() {
        assertEquals("123", ParseUtuls.removePrevix("rt123", "rt"));
    }
}