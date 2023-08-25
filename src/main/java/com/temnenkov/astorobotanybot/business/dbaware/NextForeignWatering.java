package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;

import java.io.Serializable;

public class NextForeignWatering extends NextDate {

    public NextForeignWatering(DbStore<String, Serializable> database) {
        super(database, "next.foreign.watering.timestamp");
    }
}
