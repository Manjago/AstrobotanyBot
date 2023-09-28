package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewPickPetailsScript {
    private static final Logger logger = Logger.getLogger("NewPickPetailsScript");
    private final GameClient gameClient;
    private final GardenParser gardenParser;
    public void invoke() {
        final var gardenPageState = gardenParser.parse(gameClient.wiltingPlants());
         // todo use GardenCollector methods

    }
}
