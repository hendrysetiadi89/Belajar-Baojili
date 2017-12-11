package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.combintech.baojili.application.BaseApplication;

public class PreferenceTorchHelper {

    private static final String PREF_NAME = "TorchPref";
    private static final String USE_TORCH = "USE_TORCH";

    private static SharedPreferences instance;

    private static SharedPreferences getPrefInstance(){
        if (instance == null) {
            instance = BaseApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static boolean isUseTorch(){
        return getPrefInstance().getBoolean(USE_TORCH, false);
    }

    public static void setUseTorch(boolean useTorch) {
        getPrefInstance()
                .edit()
                .putBoolean(USE_TORCH, useTorch)
                .commit();
    }
    public static void clear(){
        getPrefInstance().edit().clear().apply();
    }

}
