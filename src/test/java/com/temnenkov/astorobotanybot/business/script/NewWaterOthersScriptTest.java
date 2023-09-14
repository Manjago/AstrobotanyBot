package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    void setUp() {
        script = new NewWaterOthersScript(gameClient, newWaterOthersScriptWorker, gardenParser);
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
        when(newWaterOthersScriptWorker.processGarden(gardenPageState)).thenReturn(expectedResult);
        //when
        final NewWaterOtherScriptResult result = script.invoke();
        //then
        assertEquals(expectedResult, result);
        verifyNoMoreInteractions(gameClient, newWaterOthersScriptWorker, gardenParser);
    }
}