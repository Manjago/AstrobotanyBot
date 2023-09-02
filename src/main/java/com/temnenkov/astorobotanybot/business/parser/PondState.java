package com.temnenkov.astorobotanybot.business.parser;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public record PondState(int karma, @NotNull PetailColor blessedColor, @NotNull EnumMap<PetailColor, Integer> petails) {
}
