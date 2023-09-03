package com.temnenkov.astorobotanybot.business.parser.dto;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PlantStage {
    SEED("seed"),
    SEEDLING("seedling"),
    YOUNG("young"),
    MATURE("mature"),
    FLOWERING("flowering"),
    SEED_BEARING("seed-bearing");

    private static final PlantStage[] PLANT_STAGE_VALUES = values();

    private final String stringValue;

    PlantStage(String stringValue) {
        this.stringValue = stringValue;
    }

    @NotNull
    public static PlantStage extractFromString(@NotNull String text) {
        for (int i = PLANT_STAGE_VALUES.length - 1; i >= 0; i--) {
            PlantStage value = PLANT_STAGE_VALUES[i];
            if (text.contains(value.getStringValue())) {
                return value;
            }
        }
        throw new GeminiPanicException("Stage not found in %s".formatted(text));
    }
}
