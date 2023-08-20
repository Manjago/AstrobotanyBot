package com.temnenkov.astorobotanybot.protocol;

import org.jetbrains.annotations.Nullable;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class GeminiURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private final String pfxPath;
    private final char[] key;

    public GeminiURLStreamHandlerFactory(String pfxPath, char[] key) {
        this.pfxPath = pfxPath;
        this.key = key;
    }

    @Override
    public @Nullable URLStreamHandler createURLStreamHandler(@Nullable String protocol) {
        if ("gemini".equals(protocol)) {
            return new GeminiURLStreamHandler(pfxPath, key);
        }
        return null;
    }
}

