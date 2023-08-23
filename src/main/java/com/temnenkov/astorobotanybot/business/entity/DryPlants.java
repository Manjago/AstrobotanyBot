package com.temnenkov.astorobotanybot.business.entity;

public class DryPlants extends Garden {
    public DryPlants(String rootUrl, int waterLimit) {
        super(rootUrl, "app/garden/dry", waterLimit, "dry");
    }
}
