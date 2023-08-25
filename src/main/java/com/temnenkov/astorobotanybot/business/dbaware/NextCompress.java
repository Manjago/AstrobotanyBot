package com.temnenkov.astorobotanybot.business.dbaware;

import com.temnenkov.astorobotanybot.db.DbStore;

import java.io.Serializable;

public class NextCompress extends NextDate {

    public NextCompress(DbStore<String, Serializable> database) {
        super(database, "next.db.compress");
    }
}
