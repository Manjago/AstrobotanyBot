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
        for (PlantStage value : PLANT_STAGE_VALUES) {
            if (text.contains(value.getStringValue())) {
                return value;
            }
        }
        throw new GeminiPanicException("Stage not found in %s".formatted(text));
    }
}
