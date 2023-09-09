package com.temnenkov.astorobotanybot.protocol;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.StringJoiner;

public record GeminiContent(URL url, byte[] content, String mime, Exception exception) {
    @Override
    public String toString() {
        return new StringJoiner(", ", GeminiContent.class.getSimpleName() + "[", "]")
                .add("url=" + url)
                .add("content=" + contantAsString())
                .add("mime='" + mime + "'")
                .add("exception=" + exception)
                .toString();
    }

    @Contract(pure = true)
    private @NotNull String contantAsString() {
        if (content == null) {
            return "<null>";
        } else {
            return new String(content);
        }
    }
}