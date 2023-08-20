package com.temnenkov.astorobotanybot.protocol;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLConnection;

public class GeminiContentLoader {
    public GeminiContent loadGeminiContent (@NotNull URL url)
    {
        GeminiContent gc = new GeminiContent (url);
        try
        {
            URLConnection conn = url.openConnection();
            byte[] content = (byte[]) conn.getContent();
            String mime = conn.getContentType();
            gc.setMime (mime);
            gc.setContent (content);
        }
        catch (Exception e)
        {
            gc.setException (e);
        }
        return gc;
    }
}
