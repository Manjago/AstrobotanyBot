package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.PetailColor;
import com.temnenkov.astorobotanybot.business.parser.dto.PondState;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PondParser {
    @NotNull
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

        final Map<PetailColor, Integer> map = Arrays.stream(lines)
                .filter(s -> s.startsWith("=> /app/pond/tribute/"))
                .map(s -> ParseUtuls.removePrevix(s, "=> /app/pond/tribute/"))
                .map(s -> {
                    final var petailColor = PetailColor.parse(ParseUtuls.mid(s, "/tribute/", " "));
                    final var count = Integer.parseInt(ParseUtuls.mid(s, "Toss in ", " "));
                    return new ColorCount(petailColor, count);
                })
                .collect(Collectors.toMap(ColorCount::petailColor, ColorCount::count));

        return new PondState(karma, blessedColor, new EnumMap<>(map));
    }
    
    private record ColorCount(@NotNull PetailColor petailColor, int count) {        
    }
}

