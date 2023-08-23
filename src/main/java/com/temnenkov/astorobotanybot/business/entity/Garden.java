package com.temnenkov.astorobotanybot.business.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Garden extends GeminiAwaredEntity {
    private static final Logger logger = Logger.getLogger(Garden.class.getName());
    private final String rootUrl;
    private final String url;

    public Garden(String rootUrl, String url) {
        this.rootUrl = rootUrl;
        this.url = url;
    }

    public Garden load() {
        loadGemini(rootUrl + url);
        return this;
    }

    public boolean doWater() {
        check();
        final String[] lines = geminiContent.display().split("\\r?\\n");
        final List<String> urls = Arrays.stream(lines)
                .filter(s -> s.startsWith("=>/app/visit/"))
                .map(s -> {
                    int space = s.indexOf(" ");
                    if (space == -1) {
                        return null;
                    }
                    return s.substring(3, space);
                })
                .filter(Objects::nonNull).toList();

        if (urls.isEmpty()) {
            logger.log(Level.INFO, () -> "No found plants for watering: %s".formatted(url));
            return false;
        }
        final String plantUrl = urls.get(0);
        final var plant = new Plant(rootUrl, plantUrl).load();
        final var waterQty = plant.waterQty();
        plant.doWater();
        logger.log(Level.INFO, () -> "Plant " + plantUrl + " with waterQty " + waterQty + " watered");
        return true;
    }
}
