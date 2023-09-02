package com.temnenkov.astorobotanybot.business.parser.dto;

import org.jetbrains.annotations.NotNull;

public record PlantState(int water,
                         @NotNull PlantStage plantStage,
                         boolean hasFence,
                         boolean mayPickPetail,
                         boolean mayShakeLives,
                         boolean my) {
}
