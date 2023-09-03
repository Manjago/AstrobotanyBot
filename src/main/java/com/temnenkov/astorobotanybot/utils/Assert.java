package com.temnenkov.astorobotanybot.utils;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Assert {
    public void assertTrue(boolean condition, Supplier<String> errorMessage) {
        if (!condition) {
            throw new GeminiPanicException(errorMessage.get());
        }
    }
}
