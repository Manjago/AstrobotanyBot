package com.temnenkov.astorobotanybot.business.parser.dto;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public enum PetailColor implements Serializable {
    YELLOW("yellow"), RED("red"), ORANGE("orange"), GREEN("green"), BLUE("blue"),
    INDIGO("indigo"), VIOLET("violet"), WHITE("white"), BLACK("black"), GOLD("gold");
    private final String stringValue;

    private static final PetailColor[] VALUES = values();

    private PetailColor(String stringValue) {
        this.stringValue = stringValue;
    }

    public static @NotNull PetailColor parse(@NotNull String text) {
        for(PetailColor value : VALUES) {
            if (value.stringValue.equals(text)) {
                return value;
            }
        }
        throw new GeminiPanicException("Unexpected petail color '%s'".formatted(text));
    }

    public static @NotNull PetailColor extractFromString(@NotNull String text) {
        for(PetailColor value : VALUES) {
            if (text.contains(value.stringValue)) {
                return value;
            }
        }
        throw new GeminiPanicException("Color not found in '%s'".formatted(text));
    }
}
