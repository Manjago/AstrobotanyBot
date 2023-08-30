package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.business.dbaware.SeenTracker;
import com.temnenkov.astorobotanybot.business.entity.Garden;
import com.temnenkov.astorobotanybot.business.entity.Plant;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        logger.log(Level.INFO, () -> "%d pretenders to pick petails".formatted(urls.size()));

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
        var garden = getGarden("app/garden/flowering");
        String nextPageUrl = getNextPage(garden);

        while(nextPageUrl != null) {
            addToUrls(urls, garden);

            garden = getGarden(nextPageUrl);

            nextPageUrl = getNextPage(garden);
        }

        addToUrls(urls, garden);
        return urls;
    }

    private void addToUrls(List<String> urls, Garden garden) {
        urls.addAll(garden.getUrls("=>/app/visit/", 3).filter(seenTracker::notSeen).toList());
    }

    @NotNull
    private Garden getGarden(String nextPageUrl) {
        return new Garden(rootUrl, nextPageUrl, "flowering", geminiHelper);
    }

    @Nullable
    private String getNextPage(@NotNull Garden garden) {
        return garden.getUrls("=>/app/garden/flowering", 3, s -> s.contains("Next page")).findAny().orElse(null);
    }

}
