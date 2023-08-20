package com.temnenkov.astorobotanybot.protocol.exception;

public class GeminiPanicException extends GeminiException {
    public GeminiPanicException(Throwable cause) {
        super(cause);
    }

    public GeminiPanicException(String message) {
        super(message);
    }
}
