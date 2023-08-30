package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.business.GeminiHelper;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Plant {

    private final String rootUrl;
    @Getter
    private final String url;
    private final GeminiHelper geminiHelper;
    private final String geminiResponse;

    public Plant(String rootUrl, String url, GeminiHelper geminiHelper) {
        this.rootUrl = rootUrl;
        this.url = url;
        this.geminiHelper = geminiHelper;
        geminiResponse = geminiHelper.loadGemini(rootUrl + url);
    }

    public Plant updatedVersion() {
        return new Plant(rootUrl, url, geminiHelper);
    }

    public int waterQty() {
        int start = geminiResponse.indexOf("water :");
        if (start < 0) {
            throw new GeminiPanicException("not found water");
        }
        final String waterStart = geminiResponse.substring(start);
        int finish = waterStart.indexOf("%\n");
        if (finish < 0) {
            throw new GeminiPanicException("not found percent");
        }
        final String water = waterStart.substring(0, finish);
        final StringBuilder sb = new StringBuilder();
        for (int i = water.length() - 1; i >= 0; --i) {
            final var ch = water.charAt(i);
            if (ch == ' ') {
                break;
            }
            sb.append(ch);
        }
        return Integer.parseInt(sb.reverse().toString());
    }

    public boolean hasFence() {
        return geminiResponse.contains("fence :");
    }

    public void doWater() {
        geminiHelper.doAction(rootUrl + url + "/water");
    }

    public void doSnakeLeaves() {
        geminiHelper.doAction(rootUrl + url + "/shake");
    }

    public void pickPetal() {
        geminiHelper.doAction(rootUrl + url + "/search");
    }

    @Nullable
    public String stageString() {
        final String[] lines = geminiResponse.split("\\r?\\n");
        return Arrays.stream(lines)
                .filter(s -> s.startsWith("stage :")) // ?
                .findAny().orElse(null);
    }
}
