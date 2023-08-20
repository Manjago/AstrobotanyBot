package com.temnenkov.astorobotanybot.protocol;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class GeminiURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) {
        return new GeminiConnection(url);
    }

    @Override
    protected int getDefaultPort() {
        return 1965;
    }
}
