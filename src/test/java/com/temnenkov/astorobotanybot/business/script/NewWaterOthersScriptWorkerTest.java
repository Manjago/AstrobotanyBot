package com.temnenkov.astorobotanybot.business.script;


import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class NewWaterOthersScriptWorkerTest {

    @InjectMocks
    private NewWaterOthersScriptWorker worker;
    @Mock
    private GameClient gameClient;
    @Mock
    private PlantParser plantParser;


    @Test
    void processGardenNoPretenders() {
        //given
        final var gardenPageState = new GardenPageState(Map.of(), null);
        final Function<GardenPageState, Map<String, String>> dummy = s -> Map.of();
        //when
        final NewWaterOtherScriptResult result = worker.processGarden(gardenPageState, dummy);
        //then
        assertEquals(NewWaterOtherScriptResult.NoPretenders.INSTANCE, result);
        noOthersInteractions();
    }

    private void noOthersInteractions() {
        verifyNoMoreInteractions(gameClient, plantParser);
    }

}