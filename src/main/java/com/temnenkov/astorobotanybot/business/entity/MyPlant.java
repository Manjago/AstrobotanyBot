package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.GeminiHelper;

public class MyPlant extends Plant {
    public MyPlant(String rootUrl, GeminiHelper geminiHelper) {
        super(rootUrl, "app/plant", geminiHelper);
    }
}
