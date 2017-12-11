package com.combintech.baojili.application;

import com.google.gson.Gson;

/**
 * Created by Hendry Setiadi
 */

public class GsonSingleton {
    private static Gson instance;

    public static Gson getInstance() {
        if (instance == null) {
            instance = new Gson();
        }
        return instance;
    }
}
