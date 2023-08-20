package com.temnenkov.astorobotanybot.protocol;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;


@UtilityClass
public class GeminiContentLoader {
    private static final Logger logger = Logger.getLogger(GeminiContentLoader.class.getName());

    public static GeminiContent loadGeminiContent(@NotNull URL url) {
        final var geminiContent = new GeminiContent(url);
        try {
            URLConnection conn = url.openConnection();
            byte[] content = (byte[]) conn.getContent();
            String mime = conn.getContentType();
            geminiContent.setMime(mime);
            geminiContent.setContent(content);
        } catch (Exception e) {
            geminiContent.setException(e);
        }
        return geminiContent;
    }
}
