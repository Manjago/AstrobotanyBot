package com.temnenkov.astorobotanybot.business.parser.dto;

import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PlantStage {
    SEED("seed", 4),
    SEEDLING("seedling", 3),
    YOUNG("young", 2),
    MATURE("mature", 1),
    FLOWERING("flowering", 0),
    SEED_BEARING("seed-bearing", 5);

    private static final PlantStage[] PLANT_STAGE_VALUES = values();

    private final String stringValue;
    private final int wateringPriority;

    PlantStage(String stringValue, int wateringPriority) {
        this.stringValue = stringValue;
        this.wateringPriority = wateringPriority;
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
