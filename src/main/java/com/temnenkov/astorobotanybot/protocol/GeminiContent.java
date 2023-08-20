package com.temnenkov.astorobotanybot.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URL;

@RequiredArgsConstructor
@Getter
@Setter
public class GeminiContent {
    private final URL url;
    private byte[] content;
    private String mime;
    private Exception exception;
}
