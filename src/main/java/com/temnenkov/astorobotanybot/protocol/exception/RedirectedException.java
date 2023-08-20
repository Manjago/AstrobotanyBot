package com.temnenkov.astorobotanybot.protocol.exception;

import lombok.Getter;

import java.net.URL;

@Getter
public class RedirectedException extends GeminiException {
    final URL url;

    public RedirectedException(URL url) {
        super("Redirected to " + url.toString());
        this.url = url;
    }
}


