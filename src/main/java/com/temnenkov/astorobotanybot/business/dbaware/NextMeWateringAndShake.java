package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;

import java.io.Serializable;

public class NextMeWateringAndShake extends NextDate {

    public NextMeWateringAndShake(DbStore<String, Serializable> database) {
        super(database, "next.me.watering.and.shake.timestamp");
    }
}
