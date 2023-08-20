package com.temnenkov.astorobotanybot.protocol.exception;

import lombok.Getter;

import java.net.URL;


@Getter
public class ErrorResponseException extends GeminiException {
  private final URL url;
  private final int status;

  public ErrorResponseException(URL url, int status, String message) {
    super(message);
    this.url = url;
    this.status = status;
  }
}


