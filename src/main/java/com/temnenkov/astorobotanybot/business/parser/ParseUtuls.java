package com.temnenkov.astorobotanybot.business.parser;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ParseUtuls {
    public String removePrevix(@NotNull String source, @NotNull String prefix) {
        final int prefixLen = prefix.length();
        return source.substring(prefixLen);
    }
}
