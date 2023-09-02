package com.temnenkov.astorobotanybot.business.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PondParserTest {

    private PondParser pondParser;

    @BeforeEach
    void setUp() {
        pondParser = new PondParser();
    }

    @Test
    void parseYellow() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/pondYellow.txt"));
        //when
        final var pondState = pondParser.parse(geminiText);
        //then
        assertEquals(2567, pondState.karma());
        assertEquals(PetailColor.YELLOW, pondState.blessedColor());
    }
}