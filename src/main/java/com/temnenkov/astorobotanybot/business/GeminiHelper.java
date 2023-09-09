package com.temnenkov.astorobotanybot.business;

import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import com.temnenkov.astorobotanybot.protocol.exception.RedirectedException;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiHelper {
    private static final Logger logger = Logger.getLogger("GeminiHelper");
    @NotNull
    public String loadGemini(@NotNull String fullUrl) {
        try {
            final var geminiContent = GeminiContentLoader.loadGeminiContent(new URL(fullUrl));
            logger.log(Level.FINE, () -> "Load '%s'".formatted(geminiContent));
            if (geminiContent.exception() != null) {
                throw new GeminiPanicException("Exception happens on load url %s".formatted(fullUrl), geminiContent.exception());
            }
            if (geminiContent.content() == null) {
                throw new GeminiPanicException("Empty content on load url %s".formatted(fullUrl));
            }
            return new String(geminiContent.content());
        } catch (MalformedURLException e) {
            throw new GeminiPanicException("Bad url on load url %s".formatted(fullUrl), e);
        }
    }

    public void doAction(@NotNull String fullUrl) {
        try {
            final var geminiContent = GeminiContentLoader.loadGeminiContent(new URL(fullUrl));
            logger.log(Level.FINE, () -> "Action '%s'".formatted(geminiContent));
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        } catch (RedirectedException e) {
            // do nothing
        }
    }

}
