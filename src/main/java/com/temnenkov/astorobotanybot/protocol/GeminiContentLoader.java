package com.temnenkov.astorobotanybot.protocol;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLConnection;


@UtilityClass
public class GeminiContentLoader {

    public static GeminiContent loadGeminiContent(@NotNull URL url) {
        try {
            URLConnection conn = url.openConnection();
            byte[] content = (byte[]) conn.getContent();
            String mime = conn.getContentType();
            return new GeminiContent(url, content, mime, null);
        } catch (Exception e) {
            return new GeminiContent(url, null, null, e);
        }
    }
}
