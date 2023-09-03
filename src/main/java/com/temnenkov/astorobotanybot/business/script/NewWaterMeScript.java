package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.Loader;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;
import com.temnenkov.astorobotanybot.utils.Assert;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewWaterMeScript {
    private static final Logger logger = Logger.getLogger("NewWaterMeScript");
    private final Loader loader;
    private final PlantParser plantParser;

    public void invoke(int waterLimit) {
        final var plantState = plantParser.parse(loader.myPlant());
        Assert.assertTrue(plantState.my(), () -> "Not my plant");
        if (plantState.water() < waterLimit) {
            loader.waterMyPlant();
            final PlantState afterWatering = plantParser.parse(loader.myPlant());

        }
    }
}
