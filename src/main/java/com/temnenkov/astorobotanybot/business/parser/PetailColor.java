package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

public enum PetailColor {
    YELLOW("yellow"), RED("red"), ORANGE("orange"), GREEN("green"), BLUE("blue"),
    INDIGO("indigo"), VIOLET("violet"), WHITE("white"), BLACK("black"), GOLD("gold");
    private final String text;

    private static final PetailColor[] VALUES = values();

    PetailColor(String text) {
        this.text = text;
    }

    public static @NotNull PetailColor parse(@NotNull String text) {
        for(PetailColor value : VALUES) {
            if (value.text.equals(text)) {
                return value;
            }
        }
        throw new GeminiPanicException("Unexpected petail color '%s'".formatted(text));
    }

    public static @NotNull PetailColor extractFromString(@NotNull String text) {
        for(PetailColor value : VALUES) {
            if (text.contains(value.text)) {
                return value;
            }
        }
        throw new GeminiPanicException("Color not found in '%s'".formatted(text));
    }
}
