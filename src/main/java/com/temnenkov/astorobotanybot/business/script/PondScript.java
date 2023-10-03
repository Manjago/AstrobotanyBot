package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.parser.PondParser;
import com.temnenkov.astorobotanybot.business.parser.dto.PetailColor;
import com.temnenkov.astorobotanybot.business.parser.dto.PondState;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PondScript {
    private static final Logger logger = Logger.getLogger("PondScript");
    private @NotNull GameClient gameClient;
    private @NotNull PondParser pondParser;

    @NotNull
    public PetailColor invoke() {
        final PondState pondState = pondParser.parse(gameClient.pond());
        logger.log(Level.INFO, () -> "Pond state %s".formatted(pondState));
        return pondState.blessedColor();
    }
}
