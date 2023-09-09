package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GardenParser {
    @NotNull
    public GardenPageState parse(@NotNull String geminiText) {
        final String[] lines = geminiText.split("\\r?\\n");

        final Map<String, String> map = Arrays.stream(lines).filter(s -> s.startsWith("=>/app/visit/"))
                .map(s -> ParseUtuls.removePrevix(s, "=>/app/visit/"))
                .map(s -> new String[]{ParseUtuls.saveBefore(s, " "), ParseUtuls.saveAfter(s, " ")})
                .collect(Collectors.toMap(ar -> ar[0], ar -> ar[1]));

        final var nextPageUrl = Arrays.stream(lines).filter(s -> s.contains("Next page"))
                .map(s -> ParseUtuls.mid(s, "=>/", " "))
                .findAny().orElse(null);

        return new GardenPageState(map, nextPageUrl);
    }
}
