package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.GameClient;
import com.temnenkov.astorobotanybot.business.LogUtils;
import com.temnenkov.astorobotanybot.business.parser.GardenParser;
import com.temnenkov.astorobotanybot.business.parser.dto.GardenPageState;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GardenCollector {
    private static final Logger logger = Logger.getLogger("GardenCollector");
    private final GameClient gameClient;
    private final GardenParser gardenParser;

    @NotNull
    Map<String, String> collectIdToStatus(@NotNull GardenPageState gardenPageState) {
        final Map<String, String> toWater = new HashMap<>(gardenPageState.idToStatus());
        GardenPageState currentPage = gardenPageState;
        LogUtils.logFine(logger, currentPage);
        while (currentPage.nextPage() != null) {
            currentPage = gardenParser.parse(gameClient.justLoad(currentPage.nextPage()));
            LogUtils.logFine(logger, currentPage);
            toWater.putAll(currentPage.idToStatus());
        }
        return toWater;
    }

}
