package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.combintech.baojili.application.BaseApplication;
import com.combintech.baojili.model.LoginResponse;

public class PreferenceReminderHelper {

    private static final String PREF_NAME = "ReminderPref";
    private static final String HAS_REMIND_LOC = "REMIND_LOC";

    private static SharedPreferences instance;

    private static SharedPreferences getPrefInstance(){
        if (instance == null) {
            instance = BaseApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static boolean isAlreadyRemindLoc(){
        return getPrefInstance().getBoolean(HAS_REMIND_LOC, false);
    }

    public static void setAlreadySetLoc() {
        getPrefInstance()
                .edit()
                .putBoolean(HAS_REMIND_LOC, true)
                .commit();
    }
    public static void clear(){
        getPrefInstance().edit().clear().apply();
    }

}
