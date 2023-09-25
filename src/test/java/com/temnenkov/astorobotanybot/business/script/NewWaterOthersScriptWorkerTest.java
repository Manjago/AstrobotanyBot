package com.temnenkov.astorobotanybot.business.script;


import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

    @Test
    void pickFlowering() {
        //given
        final var gardenPageState = new GardenPageState(Map.of("1", "mature " + "1", "2", "seed 2", "3",
                "flowering " + "3"), null);
        //when
        final NewWaterOtherScriptResult result = worker.processGarden(gardenPageState, GardenPageState::idToStatus,
                l -> {
            if (l.get(0).plantStage() == PlantStage.FLOWERING) {
                return new NewWaterOtherScriptResult.Watered(10, 100);
            } else {
                return NewWaterOtherScriptResult.NoPretenders.INSTANCE;
            }
        });
        //then
        assertEquals(new NewWaterOtherScriptResult.Watered(10, 100), result);
    }

    @Test
    void waterByPriority() {
        // 4 plants, 1 with fence, 2 with 100 water, 3 - water 50
        // we should be pick plant 3
        //given
        final List<NewWaterOthersScriptWorker.IdToStage> data = List.of(new NewWaterOthersScriptWorker.IdToStage("1",
                PlantStage.FLOWERING), new NewWaterOthersScriptWorker.IdToStage("2", PlantStage.FLOWERING),
                new NewWaterOthersScriptWorker.IdToStage("3", PlantStage.FLOWERING),
                new NewWaterOthersScriptWorker.IdToStage("4", PlantStage.FLOWERING));

        when(gameClient.plant("1")).thenReturn("1-");
        when(gameClient.plant("2")).thenReturn("2-");
        when(gameClient.plant("3")).thenReturn("3-");

        when(plantParser.parse("1-")).thenReturn(new PlantState(1, PlantStage.FLOWERING, true, true, true, false, 0));
        when(plantParser.parse("2-")).thenReturn(new PlantState(100, PlantStage.FLOWERING, false, true, true, false,
                0));
        when(plantParser.parse("3-")).thenReturn(new PlantState(50, PlantStage.FLOWERING, false, true, true, false,
                0), new PlantState(100, PlantStage.FLOWERING, false, true, true, false, 0));

        //when
        final NewWaterOtherScriptResult result = worker.waterByPriority(data);
        //then
        assertEquals(new NewWaterOtherScriptResult.Watered(50, 100), result);
        verify(gameClient, times(1)).waterPlant("3");
        noOthersInteractions();
    }

    @Test
    void waterTooEarly() {
        // 4 plants, 1 with fence, 2 with 100 water, 3 - water 50, but too early
        // we should be pick plant 3
        //given
        final List<NewWaterOthersScriptWorker.IdToStage> data = List.of(new NewWaterOthersScriptWorker.IdToStage("1",
                PlantStage.FLOWERING), new NewWaterOthersScriptWorker.IdToStage("2", PlantStage.FLOWERING),
                new NewWaterOthersScriptWorker.IdToStage("3", PlantStage.FLOWERING),
                new NewWaterOthersScriptWorker.IdToStage("4", PlantStage.FLOWERING));

        when(gameClient.plant("1")).thenReturn("1-");
        when(gameClient.plant("2")).thenReturn("2-");
        when(gameClient.plant("3")).thenReturn("3-");

        when(plantParser.parse("1-")).thenReturn(new PlantState(1, PlantStage.FLOWERING, true, true, true, false, 0));
        when(plantParser.parse("2-")).thenReturn(new PlantState(100, PlantStage.FLOWERING, false, true, true, false,
                0));
        final PlantState stateBefore = new PlantState(50, PlantStage.FLOWERING, false, true, true, false,
                0);
        final PlantState ststeAfter = new PlantState(50, PlantStage.FLOWERING, false, true, true, false, 0);
        when(plantParser.parse("3-")).thenReturn(stateBefore, ststeAfter);

        //when
        final NewWaterOtherScriptResult result = worker.waterByPriority(data);
        //then
        assertEquals(new NewWaterOtherScriptResult.TooEarly(stateBefore, ststeAfter), result);
        verify(gameClient, times(1)).waterPlant("3");
        noOthersInteractions();
    }

    @Test
    void waterNoPretenders() {
        // 2 plants, 1 with fence, 2 with 100 water
        // no pretenders
        //given
        final List<NewWaterOthersScriptWorker.IdToStage> data = List.of(new NewWaterOthersScriptWorker.IdToStage("1",
                PlantStage.FLOWERING), new NewWaterOthersScriptWorker.IdToStage("2", PlantStage.FLOWERING));

        when(gameClient.plant("1")).thenReturn("1-");
        when(gameClient.plant("2")).thenReturn("2-");

        when(plantParser.parse("1-")).thenReturn(new PlantState(1, PlantStage.FLOWERING, true, true, true, false, 0));
        when(plantParser.parse("2-")).thenReturn(new PlantState(100, PlantStage.FLOWERING, false, true, true, false,
                0));

        //when
        final NewWaterOtherScriptResult result = worker.waterByPriority(data);
        //then
        assertEquals(NewWaterOtherScriptResult.NoPretenders.INSTANCE, result);
        noOthersInteractions();
    }

    private void noOthersInteractions() {
        verifyNoMoreInteractions(gameClient, plantParser);
    }

}