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
class NewWaterOthersScriptTest {

    private NewWaterOthersScript script;
    @Mock
    private GameClient gameClient;
    @Mock
    private NewWaterOthersScriptWorker newWaterOthersScriptWorker;
    @Mock
    private GardenParser gardenParser;
    @InjectMocks
    private GardenCollector gardenCollector;

    @BeforeEach
    void setUp() {
        script = new NewWaterOthersScript(gameClient, newWaterOthersScriptWorker, gardenParser, gardenCollector);
    }

    @Test
    void wilting() {
        //given
        when(gameClient.wiltingPlants()).thenReturn("1");
        final GardenPageState gardenPageState = new GardenPageState(
                Map.of("1", "2"), null
        );
        when(gardenParser.parse("1")).thenReturn(gardenPageState);
        final NewWaterOtherScriptResult.Watered expectedResult = new NewWaterOtherScriptResult.Watered(10, 100);
        when(newWaterOthersScriptWorker.processGarden(eq(gardenPageState), any())).thenReturn(expectedResult);
        //when
        final NewWaterOtherScriptResult result = script.invoke();
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

        final NewWaterOtherScriptResult.Watered expectedResult = new NewWaterOtherScriptResult.Watered(10, 100);
        when(newWaterOthersScriptWorker.processGarden(eq(notEmptyGardenPageState), any())).thenReturn(expectedResult);
        //when
        final NewWaterOtherScriptResult result = script.invoke();
        //then
        assertEquals(expectedResult, result);
        noOthersInteractions();
    }

    private void noOthersInteractions() {
        verifyNoMoreInteractions(gameClient, newWaterOthersScriptWorker, gardenParser);
    }

}