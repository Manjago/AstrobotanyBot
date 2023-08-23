package com.temnenkov.astorobotanybot.business.entity;

import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import com.temnenkov.astorobotanybot.protocol.exception.RedirectedException;

import java.net.MalformedURLException;
import java.net.URL;

public class Plant extends GeminiAwaredEntity{

    private final String rootUrl;
    private final String url;

    public Plant(String rootUrl, String url) {
        this.rootUrl = rootUrl;
        this.url = url;
    }

    public Plant load() {
        loadGemini(rootUrl + url);
        return this;
    }

    public int waterQty() {
        check();

        final String s = new String(geminiContent.getContent());
        int start = s.indexOf("water :");
        if (start < 0) {
            throw new GeminiPanicException("not found water");
        }
        final String waterStart = s.substring(start);
        int finish = waterStart.indexOf("%\n");
        if (finish < 0) {
            throw new GeminiPanicException("not found percent");
        }
        final String water = waterStart.substring(0, finish);
        final StringBuilder sb = new StringBuilder();
        for(int i = water.length() - 1; i>=0; --i) {
            final var ch = water.charAt(i);
            if (ch == ' ') {
                break;
            }
            sb.append(ch);
        }
        return Integer.parseInt(sb.reverse().toString());
    }

    public void doWater() {
        try {
            GeminiContentLoader.loadGeminiContent(new URL(rootUrl + url + "/water"));
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        } catch (RedirectedException e) {
            // do nothing
        }
    }
    public void doSnakeLeaves() {
        try {
            GeminiContentLoader.loadGeminiContent(new URL(rootUrl + url + "/shake"));
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        } catch (RedirectedException e) {
            // do nothing
        }
    }

}
