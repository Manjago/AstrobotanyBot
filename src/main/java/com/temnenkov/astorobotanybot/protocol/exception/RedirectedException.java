package com.temnenkov.astorobotanybot.protocol.exception;

import java.net.URL;

public class RedirectedException extends GeminiException
  {
  URL url;

  public RedirectedException(URL url)
    {
    super ("Redirected to " + url.toString());
    this.url = url;
    }

  public URL getURL() { return url; }
  }


