package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import org.jetbrains.annotations.NotNull;

public class WiltingPlants extends Garden {
    public WiltingPlants(String rootUrl, @NotNull GeminiHelper geminiHelper) {
        super(rootUrl, "app/garden/wilting", "wilting", geminiHelper);
    }
}
