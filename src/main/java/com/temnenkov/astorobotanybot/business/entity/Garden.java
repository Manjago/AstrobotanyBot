package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Garden extends GeminiAwareEntity {
    private static final Logger logger = Logger.getLogger("Garden");
    private final String rootUrl;
    private final String url;
    private final int waterLimit;
    private final String type;
    private final Stage[] ORDER = {Stage.FLOWERING, Stage.MATURE, Stage.YOUNG,
            Stage.SEEDLING, Stage.SEED, Stage.SEED_BEARING};

    public Garden(String rootUrl, String url, int waterLimit, String type) {
        this.rootUrl = rootUrl;
        this.url = url;
        this.waterLimit = waterLimit;
        this.type = type;
    }

    public Garden load() {
        loadGemini(rootUrl + url);
        return this;
    }

    public WateringResult doWater() {
        check();
        final String[] lines = geminiContent.display().split("\\r?\\n");
        final List<String> urls = Arrays.stream(lines).filter(s -> s.startsWith("=>/app/visit/")).map(s -> {
            int space = s.indexOf(" ");
            if (space == -1) {
                return null;
            }
            return s.substring(3, space);
        }).filter(Objects::nonNull).limit(5).toList(); //todo to config
        //todo parse text from description

        if (urls.isEmpty()) {
            logger.log(Level.INFO, () -> "No found plants for watering: %s".formatted(url));
            return WateringResult.NOT_FOUND;
        }

        final List<WaterInfo> waterInfos = waterInfoByUrls(urls);

        for(Stage stage : ORDER) {
           final WateringResult result = doWater(byStage(waterInfos, stage));
           if (result.isTerminal()) {
               return result;
           }
        }

        return WateringResult.NOT_FOUND;
    }

    private WateringResult doWater(@NotNull List<WaterInfo> waterInfos) {
        for (WaterInfo waterInfo : waterInfos) {
            if (waterInfo.waterQty < waterLimit) {
                var oldWaterQty = waterInfo.waterQty;
                waterInfo.plant.doWater();
                logger.log(Level.INFO, () -> "%s %s plant %s with waterQty %d watered".formatted(waterInfo.stage, type, waterInfo.plant.getUrl(), oldWaterQty));

                waterInfo.plant.load();
                int newWaterQty = waterInfo.plant.waterQty();
                if (newWaterQty == oldWaterQty) {
                    return WateringResult.TOO_EARLY;
                }

                return WateringResult.WATERED;
            }
        }
        return WateringResult.NOT_FOUND;
    }

    @NotNull
    private List<WaterInfo> waterInfoByUrls(@NotNull List<String> urls) {
        return urls.stream().map(purl -> {
            final var plant = new Plant(rootUrl, purl).load();
            if (plant.hasFence()) {
                return null;
            }
            final var waterQty = plant.waterQty();
            final String stageString = plant.stageString();
            final Stage stage = Stage.getStage(stageString);

            if (stage != null) {
                return new WaterInfo(plant, waterQty, stage);
            }

            return null;
        }).filter(Objects::nonNull).toList();
    }

    @NotNull
    private List<WaterInfo> byStage(@NotNull List<WaterInfo> waterInfos, @NotNull Stage stage) {
        return waterInfos.stream().filter(waterInfo -> stage == waterInfo.stage).toList();
    }

    private record WaterInfo(Plant plant, int waterQty, Stage stage) {
    }

}
