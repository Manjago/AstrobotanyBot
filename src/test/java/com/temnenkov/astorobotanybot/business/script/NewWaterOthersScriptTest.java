package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

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

    //@Test
    void wilting() {
        //given
        Mockito.when(gameClient.myPlant()).thenReturn("1");
        Mockito.when(gardenParser.parse("1")).thenReturn(new GardenPageState(
                Map.of("1", "2"), null
        ));
        //when
        script.invoke();
        //then
    }
}