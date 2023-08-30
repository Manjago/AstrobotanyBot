package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.business.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Garden {
    private static final Logger logger = Logger.getLogger("Garden");
    private final String rootUrl;
    protected final String url;
    private final String type;
    protected final GeminiHelper geminiHelper;
    private static final Stage[] ORDER = {Stage.FLOWERING, Stage.MATURE, Stage.YOUNG,
            Stage.SEEDLING, Stage.SEED, Stage.SEED_BEARING};
    private final String geminiResponse;

    public Garden(String rootUrl, String url, String type, GeminiHelper geminiHelper) {
        this.rootUrl = rootUrl;
        this.url = url;
        this.type = type;
        this.geminiHelper = geminiHelper;
        geminiResponse = geminiHelper.loadGemini(rootUrl + url);
    }

    public WateringResult doWater(int waterLimit, long limit) {
        final List<String> urls = getUrls("=>/app/visit/", 3).limit(limit).toList();

        if (urls.isEmpty()) {
            logger.log(Level.INFO, () -> "No found plants for watering: %s".formatted(url));
            return WateringResult.NOT_FOUND;
        }

        final List<WaterInfo> waterInfos = waterInfoByUrls(urls);

        for(Stage stage : ORDER) {
           final WateringResult result = doWater(byStage(waterInfos, stage), waterLimit);
           if (result.isTerminal()) {
               return result;
           }
        }

        return WateringResult.NOT_FOUND;
    }

    @NotNull
    public Stream<String> getUrls(@NotNull String prefix, int removeFromHead) {
        final String[] lines = geminiResponse.split("\\r?\\n");
        return Arrays.stream(lines).filter(s -> s.startsWith(prefix)).map(s -> {
            int space = s.indexOf(" ");
            if (space == -1) {
                return null;
            }
            return s.substring(removeFromHead, space);
        }).filter(Objects::nonNull);
    }

    private WateringResult doWater(@NotNull List<WaterInfo> waterInfos, int waterLimit) {
        for (WaterInfo waterInfo : waterInfos) {
            if (waterInfo.waterQty < waterLimit) {
                var oldWaterQty = waterInfo.waterQty;
                waterInfo.plant.doWater();
                logger.log(Level.INFO, () -> "%s %s plant %s with waterQty %d watered".formatted(waterInfo.stage, type, waterInfo.plant.getUrl(), oldWaterQty));

                final Plant wateredPlant = waterInfo.plant.updatedVersion();
                int newWaterQty = wateredPlant.waterQty();
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
            final var plant = new Plant(rootUrl, purl, geminiHelper);
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
