package com.temnenkov.astorobotanybot.business;

import com.temnenkov.astorobotanybot.protocol.GeminiContent;
import com.temnenkov.astorobotanybot.protocol.GeminiContentLoader;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import com.temnenkov.astorobotanybot.protocol.exception.RedirectedException;

import java.net.MalformedURLException;
import java.net.URL;

public class Plant {

    private final String rootUrl;
    private final String url;

    private GeminiContent geminiContent;

    public Plant(String rootUrl, String url) {
        this.rootUrl = rootUrl;
        this.url = url;
    }

    public Plant load() {
        try {
            geminiContent = GeminiContentLoader.loadGeminiContent(new URL(rootUrl + url));
            return this;
        } catch (MalformedURLException e) {
            throw new GeminiPanicException(e);
        }
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


    private void check() {
        if (geminiContent == null) {
            throw new GeminiPanicException("Gemini not loaded");
        }
        if (geminiContent.getException() != null) {
            throw new GeminiPanicException(geminiContent.getException());
        }
        if (geminiContent.getContent() == null) {
            throw new GeminiPanicException("Empty gemini content");
        }
    }

}
