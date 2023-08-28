package com.temnenkov.astorobotanybot.business.entity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FloweringPlants extends Garden {
    private static final Logger logger = Logger.getLogger("FloweringPlants");

    public FloweringPlants(String rootUrl, int waterLimit) {
        super(rootUrl, "app/garden/flowering", waterLimit, "flowering");
    }

    public void doPickPetals() {
        check();
        final List<String> urls = getUrls(5);

        if (urls.isEmpty()) {
            logger.log(Level.INFO, () -> "No found plants for pick petals: %s".formatted(url));
            return;
        }

    }

}
