package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;

public class PondParser {
    public PondState parse(@NotNull String geminiText) {

        final String[] lines = geminiText.split("\\r?\\n");
        final var karma = Arrays.stream(lines)
                .filter(s -> s.startsWith("Your karma: "))
                .map(s -> Integer.parseInt(ParseUtuls.removePrevix(s, "Your karma: ")))
                .findAny()
                .orElseThrow(() -> new GeminiPanicException("Fail extract karma from " + geminiText));

       final var blessedColor = Arrays.stream(lines)
               .filter(s -> s.startsWith("Today's blessed color is "))
               .map(PetailColor::extractFromString)
               .findAny()
               .orElseThrow(() -> new GeminiPanicException("Fail extract current color from " + geminiText));


        return new PondState(karma, blessedColor, new EnumMap<>(PetailColor.class));
    }
}

