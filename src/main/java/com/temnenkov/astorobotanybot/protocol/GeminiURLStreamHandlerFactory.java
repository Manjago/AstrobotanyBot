package com.temnenkov.astorobotanybot.protocol;

import org.jetbrains.annotations.Nullable;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class GeminiURLStreamHandlerFactory implements URLStreamHandlerFactory {
    @Override
    public @Nullable URLStreamHandler createURLStreamHandler(@Nullable String protocol) {
        if ("gemini".equals(protocol)) {
            return new GeminiURLStreamHandler();
        }
        return null;
    }
}

