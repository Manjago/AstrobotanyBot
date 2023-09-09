package com.temnenkov.astorobotanybot.business.parser.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record GardenPageState(@NotNull Map<String, String> idToStatus, @Nullable String nextPage) {
}
