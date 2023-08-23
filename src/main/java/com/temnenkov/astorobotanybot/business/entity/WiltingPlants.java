package com.temnenkov.astorobotanybot.business.entity;

public class WiltingPlants extends Garden {
    public WiltingPlants(String rootUrl, int waterLimit) {
        super(rootUrl, "app/garden/wilting", waterLimit, "wilting");
    }
}
