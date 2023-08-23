package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.protocol.GeminiContent;
import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class GeminiAwaredEntity {
    protected GeminiContent geminiContent;

    protected void loadGemini(@NotNull String fullUrl) {
        try {
            geminiContent = GeminiContentLoader.loadGeminiContent(new URL(fullUrl));
            if (geminiContent.getException() != null) {
                throw new GeminiPanicException("Fail load plant by '%s'".formatted(fullUrl),  geminiContent.getException());
            }
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        }
    }

    protected void check() {
        if (geminiContent == null) {
            throw new GeminiPanicException("Gemini not loaded");
        }
        if (geminiContent.getException() != null) {
            throw new GeminiPanicException(geminiContent.getException());
        }
        if (geminiContent.getContent() == null) {
            throw new GeminiPanicException("Empty gemini content");
        }
    }

}
