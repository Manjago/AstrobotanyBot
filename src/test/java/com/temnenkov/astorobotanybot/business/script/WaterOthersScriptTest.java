package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaterOthersScriptTest {

    private WaterOthersScript script;
    @Mock
    private GameClient gameClient;
    @Mock
    private WaterOthersScriptWorker waterOthersScriptWorker;
    @Mock
    private GardenParser gardenParser;
    @InjectMocks
    private GardenCollector gardenCollector;

    @BeforeEach
    void setUp() {
        script = new WaterOthersScript(gameClient, waterOthersScriptWorker, gardenParser, gardenCollector);
    }

    @Test
    void wilting() {
        //given
        when(gameClient.wiltingPlants()).thenReturn("1");
        final GardenPageState gardenPageState = new GardenPageState(
                Map.of("1", "2"), null
        );
        when(gardenParser.parse("1")).thenReturn(gardenPageState);
        final WaterOtherScriptResult.Watered expectedResult = new WaterOtherScriptResult.Watered(10, 100);
        when(waterOthersScriptWorker.processGarden(eq(gardenPageState), any())).thenReturn(expectedResult);
        //when
        final WaterOtherScriptResult result = script.invoke();
        //then
        assertEquals(expectedResult, result);
        noOthersInteractions();
    }

    @Test
    void dry() {
        //given
        when(gameClient.wiltingPlants()).thenReturn("1");
        final GardenPageState emptyGardenPageState = new GardenPageState(
                Map.of(), null
        );
        when(gardenParser.parse("1")).thenReturn(emptyGardenPageState);

        when(gameClient.dryPlants()).thenReturn("2");
        final GardenPageState notEmptyGardenPageState = new GardenPageState(
                Map.of("1", "2"), null
        );
        when(gardenParser.parse("2")).thenReturn(notEmptyGardenPageState);

        final WaterOtherScriptResult.Watered expectedResult = new WaterOtherScriptResult.Watered(10, 100);
        when(waterOthersScriptWorker.processGarden(eq(notEmptyGardenPageState), any())).thenReturn(expectedResult);
        //when
        final WaterOtherScriptResult result = script.invoke();
        //then
        assertEquals(expectedResult, result);
        noOthersInteractions();
    }

    private void noOthersInteractions() {
        verifyNoMoreInteractions(gameClient, waterOthersScriptWorker, gardenParser);
    }

}