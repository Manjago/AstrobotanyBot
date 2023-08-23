package com.temnenkov.astorobotanybot.business;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum Stage {
    SEED("seed"),
    SEEDLING("seedling"),
    YOUNG("young"),
    MATURE("mature"),
    FLOWERING("flowering"),
    SEED_BEARING("seed-bearing");

    private static final Stage[] STAGE_VALUES = values();

    private final String stringValue;

    Stage(String stringValue) {
        this.stringValue = stringValue;
    }

    @Nullable
    public static Stage getStage(@Nullable String string) {
        if (string == null) {
            return null;
        }
        for (Stage value : STAGE_VALUES) {
            if (string.contains(value.getStringValue())) {
                return value;
            }
        }
        return null;
    }
}
