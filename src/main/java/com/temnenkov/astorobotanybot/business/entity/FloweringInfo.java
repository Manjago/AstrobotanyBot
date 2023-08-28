package com.temnenkov.astorobotanybot.business.entity;

import java.io.Serializable;
import java.time.Instant;

public record FloweringInfo(String url, int rate, Instant ttl) implements Serializable {
}
