package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantParserTest {

    private PlantParser plantParser;

    @BeforeEach
    void setUp() {
        plantParser = new PlantParser();
    }

    @Test
    void parseFlowering() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/flowering-plant.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(90, plantState.water());
        assertEquals(PlantStage.FLOWERING, plantState.plantStage());
        assertFalse(plantState.hasFence());
        assertTrue(plantState.mayPickPetail());
        assertFalse(plantState.mayShakeLives());
        assertFalse(plantState.my());
        assertEquals(0, plantState.coinsEarned());
    }

    @Test
    void parseDead() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/dead-plant.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(-1, plantState.water());
        assertEquals(PlantStage.SEED_BEARING, plantState.plantStage());
        assertTrue(plantState.hasFence());
        assertFalse(plantState.mayPickPetail());
        assertFalse(plantState.mayShakeLives());
        assertFalse(plantState.my());
        assertEquals(0, plantState.coinsEarned());
    }

    @Test
    void parseMy() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/my-plant.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(98, plantState.water());
        assertEquals(PlantStage.SEEDLING, plantState.plantStage());
        assertFalse(plantState.hasFence());
        assertFalse(plantState.mayPickPetail());
        assertTrue(plantState.mayShakeLives());
        assertTrue(plantState.my());
        assertEquals(0, plantState.coinsEarned());
    }

    @Test
    void parseDry() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/dry-plant.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(0, plantState.water());
        assertEquals(PlantStage.MATURE, plantState.plantStage());
        assertFalse(plantState.hasFence());
        assertFalse(plantState.mayPickPetail());
        assertFalse(plantState.mayShakeLives());
        assertFalse(plantState.my());
        assertEquals(0, plantState.coinsEarned());
    }

    @Test
    void parseCoins() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/shake-lives-ok.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(99, plantState.water());
        assertEquals(PlantStage.MATURE, plantState.plantStage());
        assertFalse(plantState.hasFence());
        assertFalse(plantState.mayPickPetail());
        assertTrue(plantState.mayShakeLives());
        assertTrue(plantState.my());
        assertEquals(18, plantState.coinsEarned());
    }

    @Test
    void parseCoinsZero() throws IOException {
        //given
        final var geminiText = Files.readString(Path.of("src/test/resources/shake-lives-0-coins.txt"));
        //when
        final var plantState = plantParser.parse(geminiText);
        //then
        assertEquals(98, plantState.water());
        assertEquals(PlantStage.MATURE, plantState.plantStage());
        assertFalse(plantState.hasFence());
        assertFalse(plantState.mayPickPetail());
        assertTrue(plantState.mayShakeLives());
        assertTrue(plantState.my());
        assertEquals(0, plantState.coinsEarned());
    }
}