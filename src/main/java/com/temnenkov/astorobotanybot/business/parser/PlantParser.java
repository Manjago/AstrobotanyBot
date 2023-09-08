package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PlantParser {
    @NotNull
    public PlantState parse(@NotNull String geminiText) {

        final String[] lines = geminiText.split("\\r?\\n");

        final int water = Arrays.stream(lines).filter(s ->  s.contains("water :"))
                .map(this::extractWater)
                .findAny().orElseThrow(() -> new GeminiPanicException("No water"));

        final PlantStage plantStage = Arrays.stream(lines).filter(s -> s.startsWith("stage : "))
                .map(PlantStage::extractFromString)
                .findAny().orElseThrow(() -> new GeminiPanicException("No stage"));

        final boolean hasFence = Arrays.stream(lines).anyMatch(s -> s.startsWith("fence :"));

        final boolean mayPickPetail = Arrays.stream(lines).anyMatch(s -> s.contains("Pick a petal"));

        final boolean mayShakeLives = Arrays.stream(lines).anyMatch(s -> s.contains("Shake leaves"));

        final boolean my = Arrays.stream(lines).anyMatch(s -> s.contains("Your plant"));

        final int coinsEarned = Arrays.stream(lines).filter( s -> s.contains(" coins)"))
                .map( s -> Integer.parseInt(ParseUtuls.mid(s,"(", " coins)"))                )
                .findAny().orElse(0);

        return new PlantState(water, plantStage, hasFence, mayPickPetail, mayShakeLives, my, coinsEarned);
    }

    private int extractWater(@NotNull String waterString) {
        int start = waterString.indexOf("water :");
        if (start < 0) {
            throw new GeminiPanicException("not found water");
        }
        final String waterStart = waterString.substring(start);
        int finish = waterStart.indexOf("%");
        if (finish < 0) {
            return -1;
        }
        final String water = waterStart.substring(0, finish);
        final StringBuilder sb = new StringBuilder();
        for (int i = water.length() - 1; i >= 0; --i) {
            final var ch = water.charAt(i);
            if (ch == ' ') {
                break;
            }
            sb.append(ch);
        }
        return Integer.parseInt(sb.reverse().toString());
    }
}
