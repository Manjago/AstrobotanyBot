package com.temnenkov.astorobotanybot.protocol.exception;

import lombok.Getter;

@Getter
public class RedirectedException extends GeminiException {
    final String url;

    public RedirectedException(String url) {
        super("Redirected to " + url);
        this.url = url;
    }
}


