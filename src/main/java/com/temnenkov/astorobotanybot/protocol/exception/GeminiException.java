package com.temnenkov.astorobotanybot.protocol.exception;

public class GeminiException extends RuntimeException {
    public GeminiException(String message) {
        super(message);
    }

    public GeminiException(Throwable cause) {
        super(cause);
    }
}
