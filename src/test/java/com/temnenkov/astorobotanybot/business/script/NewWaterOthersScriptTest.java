package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
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

    @InjectMocks
    private NewWaterOthersScript script;
    @Mock
    private GameClient gameClient;
    @Mock
    private NewWaterOthersScriptWorker newWaterOthersScriptWorker;
    @Mock
    private GardenParser gardenParser;

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

    @Test
    void loadOnePage() {
        //given
        final GardenPageState gardenPageState = new GardenPageState(Map.of("1", "2", "3", "4"), null);
        //when
        final Map<String, String> map = script.loadPlantsToWater(gardenPageState);
        //then
        assertEquals(2, map.size());
        assertEquals("4", map.get("3"));
        assertEquals("2", map.get("1"));
        noOthersInteractions();
    }

    @Test
    void loadTwoPage() {
        //given
        final String nextPage = "//next";
        final GardenPageState gardenPageState1 = new GardenPageState(Map.of("1", "2", "3", "4"), nextPage);
        final GardenPageState gardenPageState2 = new GardenPageState(Map.of("5", "6"), null);
        when(gameClient.justLoad(nextPage)).thenReturn("g");
        when(gardenParser.parse("g")).thenReturn(gardenPageState2);
        //when
        final Map<String, String> map = script.loadPlantsToWater(gardenPageState1);
        //then
        assertEquals(3, map.size());
        assertEquals("4", map.get("3"));
        assertEquals("2", map.get("1"));
        assertEquals("6", map.get("5"));
        noOthersInteractions();
    }

    private void noOthersInteractions() {
        verifyNoMoreInteractions(gameClient, newWaterOthersScriptWorker, gardenParser);
    }

}