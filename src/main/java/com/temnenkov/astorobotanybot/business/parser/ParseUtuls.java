package com.temnenkov.astorobotanybot.business.parser;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ParseUtuls {
    @NotNull
    public String removePrevix(@NotNull String source, @NotNull String prefix) {
        final int prefixLen = prefix.length();
        return source.substring(prefixLen);
    }

    @NotNull
    public String saveAfter(@NotNull String source, @NotNull String toRemove) {
        final var startPos = source.indexOf(toRemove);
        if (startPos == -1) {
            return source;
        }
        return source.substring(startPos + toRemove.length());
    }

    @NotNull
    public String saveBefore(@NotNull String source, @NotNull String toRemove) {
        final var startPos = source.indexOf(toRemove);
        if (startPos == -1) {
            return source;
        }
        return source.substring(0, startPos);
    }

    @NotNull
    public String mid(@NotNull String source, @NotNull String before, @NotNull String after) {
        return saveBefore(saveAfter(source, before), after);
    }
}
