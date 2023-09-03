package com.temnenkov.astorobotanybot.business;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class Loader {
    private final String baseUrl;
    private final GeminiHelper geminiHelper;

    @NotNull
    public String myPlant() {
        return geminiHelper.loadGemini(baseUrl + "app/plant");
    }

    public void waterMyPlant() {
        geminiHelper.doAction(baseUrl + "app/plant/water");
    }
}
