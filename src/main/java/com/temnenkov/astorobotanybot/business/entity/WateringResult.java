package com.temnenkov.astorobotanybot.business.entity;

import lombok.Getter;

@Getter
public enum WateringResult {
    WATERED(true), NOT_FOUND(false), TOO_EARLY(true);

    private final boolean terminal;

    WateringResult(boolean terminal) {
        this.terminal = terminal;
    }

}
