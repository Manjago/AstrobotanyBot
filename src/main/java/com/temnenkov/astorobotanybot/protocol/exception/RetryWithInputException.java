package com.temnenkov.astorobotanybot.protocol.exception;

import lombok.Getter;

import java.net.URL;

@Getter
public class RetryWithInputException extends GeminiException {
  private final URL url;
  private final boolean hide;
  private final String prompt;

  public RetryWithInputException(URL url, boolean hide, String prompt) {
    super("Retry with input");
    this.url = url;
    this.hide = hide;
    this.prompt = prompt;
  }

}

