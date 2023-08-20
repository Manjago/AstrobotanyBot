package com.temnenkov.astorobotanybot.protocol;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class GeminiURLStreamHandler extends URLStreamHandler {

    private final String pfxPath;
    private final char[] key;


    public GeminiURLStreamHandler(String pfxPath, char[] key) {
        this.pfxPath = pfxPath;
        this.key = key;
    }

    @Override
    protected URLConnection openConnection(URL url) {
        return new GeminiConnection(url, pfxPath, key);
    }

    @Override
    protected int getDefaultPort() {
        return 1965;
    }
}
