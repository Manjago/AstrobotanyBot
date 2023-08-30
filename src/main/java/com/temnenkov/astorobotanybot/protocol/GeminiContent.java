package com.temnenkov.astorobotanybot.protocol;

import java.net.URL;

public record GeminiContent(URL url, byte[] content, String mime, Exception exception) {}