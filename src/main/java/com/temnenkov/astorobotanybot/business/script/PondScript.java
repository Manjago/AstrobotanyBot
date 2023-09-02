package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.business.parser.PondParser;
import com.temnenkov.astorobotanybot.business.parser.PondState;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PondScript {
    private @NotNull String rootUrl;
    private @NotNull GeminiHelper geminiHelper;
    private @NotNull PondParser pondParser;

    private static final Logger logger = Logger.getLogger("PondScript");

    public void invoke() {
        try {
            final String gemini = geminiHelper.loadGemini(rootUrl + "app/pond/");
            final PondState pondState = pondParser.parse(gemini);
            logger.log(Level.INFO, () -> "Pond state " + pondState);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Fail get pond state", ex);
        }
    }
}
