package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.business.dbaware.SeenTracker;
import com.temnenkov.astorobotanybot.business.entity.Garden;
import com.temnenkov.astorobotanybot.business.entity.Plant;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PickPetalsScript {

    private static final Logger logger = Logger.getLogger("PickPetalsScript");
    private final String rootUrl;
    private final GeminiHelper geminiHelper;
    private final SeenTracker seenTracker;

    public void invoke(boolean dry) {
        seenTracker.refresh();

        final List<String> urls = getUrls();

        if (urls.isEmpty()) {
            logger.log(Level.INFO, "No plants for pick petal");
            return;
        }

        urls.forEach(s -> {
            if (!dry) {
                new Plant(rootUrl, s, geminiHelper).pickPetal();
                logger.log(Level.INFO, () -> "pick petail %s".formatted(s));
            } else {
                logger.log(Level.INFO, () -> "dry pick petail %s".formatted(s));
            }
            seenTracker.markAsSeen(s);
        });

    }

    @NotNull
    private List<String> getUrls() {
        final List<String> urls = new ArrayList<>();
        String nextPageUrl;
        var garden = new Garden(rootUrl, "app/garden/flowering", "flowering", geminiHelper);
        do {
            urls.addAll(garden.getUrls("=>/app/visit/", 3).filter(seenTracker::notSeen).toList());

            nextPageUrl = garden.getUrls("=>/app/garden/flowering", 3).filter(s -> s.contains("Next page")).findAny().orElse(null);

            if (nextPageUrl != null) {
                garden = new Garden(rootUrl, nextPageUrl, "flowering", geminiHelper);
            }
        } while (nextPageUrl != null);
        return urls;
    }

}
