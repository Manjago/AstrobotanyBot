package com.temnenkov.astorobotanybot.business;

import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import com.temnenkov.astorobotanybot.protocol.exception.RedirectedException;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public class GeminiHelper {
    @NotNull
    public String loadGemini(@NotNull String fullUrl) {
        try {
            final var geminiContent = GeminiContentLoader.loadGeminiContent(new URL(fullUrl));
            if (geminiContent.exception() != null) {
                throw new GeminiPanicException("For url %s".formatted(fullUrl), geminiContent.exception());
            }
            if (geminiContent.content() == null) {
                throw new GeminiPanicException("For url %sempty gemini content".formatted(fullUrl));
            }
            return new String(geminiContent.content());
        } catch (MalformedURLException e) {
            throw new GeminiPanicException("For url %s".formatted(fullUrl), e);
        }
    }

    public void doAction(@NotNull String fullUrl) {
        try {
            GeminiContentLoader.loadGeminiContent(new URL(fullUrl));
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        } catch (RedirectedException e) {
            // do nothing
        }
    }

}
