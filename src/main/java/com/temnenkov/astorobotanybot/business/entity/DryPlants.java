package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.GeminiHelper;

public class DryPlants extends Garden {
    public DryPlants(String rootUrl, GeminiHelper geminiHelper) {
        super(rootUrl, "app/garden/dry", "dry", geminiHelper);
    }
}
