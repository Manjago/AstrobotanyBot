package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GardenParserTest {

    private GardenParser gardenParser;

    @BeforeEach
    void setUp() {
        gardenParser = new GardenParser();
    }

    @Test
    void parseLastPage() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/dry-page-8.txt"));
        //when
        final GardenPageState state = gardenParser.parse(geminiText);
        //then
        assertNull(state.nextPage());
        assertEquals(7, state.idToStatus().size());
        assertEquals("cosmic young snapdragon", state.idToStatus().get("8d1cf2bd465144e8a0a63873aa0553de"));
        assertEquals("deformed young iris", state.idToStatus().get("048a43ae13c34628af01b89a8c3a851a"));
        assertEquals("chalky young hemp", state.idToStatus().get("cfea2879d5ef405291917d71a8b0b72e"));
        assertEquals("gnu/linux young pachypodium", state.idToStatus().get("eb57626563b240e7a522af1be9c4d0e2"));
        assertEquals("fractal young sage", state.idToStatus().get("07ab2765fdd6470eab16515094549102"));
        assertEquals("smug young pansy", state.idToStatus().get("c43e9ece65554a2da381d6f3a57957d1"));
        assertEquals("seedling", state.idToStatus().get("0adf613b7493491a8c3ceca1280cc450"));
    }

    @Test
    void parseFirstPage() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/dry-page-1.txt"));
        //when
        final GardenPageState state = gardenParser.parse(geminiText);
        //then
        assertEquals("app/garden/dry/2", state.nextPage());
        assertEquals(20, state.idToStatus().size());
        assertEquals("common narcotic orange seed-bearing baobab", state.idToStatus().get("3a10baa1b0b8496eb2afbbecc3a80649"));
    }

    @Test
    void parseEmptyPage() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/empty-wilting.txt"));
        //when
        final GardenPageState state = gardenParser.parse(geminiText);
        //then
        assertNull(state.nextPage());
        assertTrue(state.idToStatus().isEmpty());
    }
}