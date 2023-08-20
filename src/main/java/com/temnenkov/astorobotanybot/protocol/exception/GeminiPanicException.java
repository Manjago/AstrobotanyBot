package com.temnenkov.astorobotanybot.protocol.exception;

public class GeminiPanicException extends GeminiException {
    public GeminiPanicException(String message) {
        super(message);
    }

    public GeminiPanicException(Throwable cause) {
        super(cause);
    }
}
