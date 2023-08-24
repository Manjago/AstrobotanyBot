package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Garden extends GeminiAwareEntity {
    private static final Logger logger = Logger.getLogger(Garden.class.getName());
    private final String rootUrl;
    private final String url;
    private final int waterLimit;
    private final String type;

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

    public boolean doWater() {
        check();
        final String[] lines = geminiContent.display().split("\\r?\\n");
        final List<String> urls = Arrays.stream(lines).filter(s -> s.startsWith("=>/app/visit/")).map(s -> {
            int space = s.indexOf(" ");
            if (space == -1) {
                return null;
            }
            return s.substring(3, space);
        }).filter(Objects::nonNull).toList();

        if (urls.isEmpty()) {
            logger.log(Level.INFO, () -> "No found plants for watering: %s".formatted(url));
            return false;
        }

        final List<Info> infos = infoByUrls(urls);
        if (doWater(byStage(infos, Stage.FLOWERING))) {
            return true;
        } else if (doWater(byStage(infos, Stage.MATURE))) {
            return true;
        } else if (doWater(byStage(infos, Stage.YOUNG))) {
            return true;
        } else if (doWater(byStage(infos, Stage.SEEDLING))) {
            return true;
        } else if (doWater(byStage(infos, Stage.SEED))) {
            return true;
        } else {
            return doWater(byStage(infos, Stage.SEED_BEARING));
        }
    }

    private boolean doWater(@NotNull List<Info> infos) {
        for (Info info : infos) {
            if (info.waterQty < waterLimit) {
                info.plant.doWater();
                logger.log(Level.INFO, () -> "%s %s plant %s with waterQty %d watered".formatted(info.stage, type, info.plant.getUrl(), info.waterQty));
                return true;
            }
        }
        return false;
    }

    @NotNull
    private List<Info> infoByUrls(@NotNull List<String> urls) {
        return urls.stream().map(purl -> {
            final var plant = new Plant(rootUrl, purl).load();
            final var waterQty = plant.waterQty();
            final String stageString = plant.stageString();
            final Stage stage = Stage.getStage(stageString);

            if (stage != null) {
                return new Info(plant, waterQty, stage);
            }

            return null;
        }).filter(Objects::nonNull).toList();
    }

    @NotNull
    private List<Info> byStage(@NotNull List<Info> infos, @NotNull Stage stage) {
        return infos.stream().filter(info -> stage == info.stage).toList();
    }

    private record Info(Plant plant, int waterQty, Stage stage) {
    }
}
