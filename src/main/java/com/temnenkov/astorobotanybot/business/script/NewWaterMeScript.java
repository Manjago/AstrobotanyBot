package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import com.temnenkov.astorobotanybot.utils.Assert;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NewWaterMeScript {
    private static final Logger logger = Logger.getLogger("NewWaterMeScript");
    private final GameClient gameClient;
    private final PlantParser plantParser;
    private final int waterLimit;

    public NewWaterMeScriptResult invoke() {
        final var stateBefore = plantParser.parse(gameClient.myPlant());
        Assert.assertTrue(stateBefore.my(), () -> "Not my plant");

        if (stateBefore.water() < waterLimit) {
            gameClient.waterMyPlant();
            final var stateAfter = plantParser.parse(gameClient.myPlant());
            if (stateAfter.water() == stateBefore.water()) {
                logger.log(Level.WARNING, () -> "I water plant, but no changes: stateBefore " + stateBefore + ", stateAfter " + stateAfter);
                return new NewWaterMeScriptResult.NoChanges(stateBefore, stateAfter);
            } else {
                logger.log(Level.INFO, () -> "I watered my plant, was %d now %d".formatted(stateBefore.water(), stateAfter.water()));
                return new NewWaterMeScriptResult.Watered(stateBefore.water(), stateAfter.water());
            }
        } else {
            logger.log(Level.FINE, () -> "PlantState is %s, waterLimit is %d - do nothing".formatted(stateBefore, waterLimit));
            return new NewWaterMeScriptResult.DoNothing(stateBefore, waterLimit);
        }
    }
}
