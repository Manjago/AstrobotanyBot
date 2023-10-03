package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PlantParser;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ShakeLivesScript {
    private static final Logger logger = Logger.getLogger("ShakeLivesScript");
    private final GameClient gameClient;
    private final PlantParser plantParser;

    public ShakeLivesScriptResult invoke() {
        final var stateBefore = plantParser.parse(gameClient.myPlant());
        if (!stateBefore.mayShakeLives()) {
            logger.log(Level.WARNING, () -> "Shake lives not allowed?! %s".formatted(stateBefore));
            return new ShakeLivesScriptResult(0);
        }

        gameClient.shakeLives();
        final var stateAfter = plantParser.parse(gameClient.myPlant());
        logger.log(Level.INFO, () -> "Earned " + stateAfter.coinsEarned() + " coins");
        return new ShakeLivesScriptResult(stateAfter.coinsEarned());
    }
}
