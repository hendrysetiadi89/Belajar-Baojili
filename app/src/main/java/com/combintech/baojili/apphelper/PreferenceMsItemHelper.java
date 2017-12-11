package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.combintech.baojili.application.BaseApplication;
import com.combintech.baojili.application.GsonSingleton;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class PreferenceMsItemHelper {
    private static final String PREF_NAME = "ItemPref";

    private static final String MS_ITEM = "ms_item";
    private static final String TIMESTAMP = "timestamp";

    private static final int EXPIRY_TIME = 300; // 5 min

    private static SharedPreferences instance;

    private static SharedPreferences getPrefInstance(){
        if (instance == null) {
            instance = BaseApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static long getTimeStamp(){
        return getPrefInstance().getLong(TIMESTAMP, 0);
    }

    public static ArrayList<BJItem> getMsItem() {
        String jsonString = getPrefInstance().getString(MS_ITEM, "");
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Type listType = new TypeToken< ArrayList<BJItem> >(){}.getType();
        return GsonSingleton.getInstance().fromJson(jsonString, listType);
    }

    public static void setMsItem(ArrayList<BJItem> bjItemArrayList){
        getPrefInstance()
                .edit()
                .putLong(TIMESTAMP, System.currentTimeMillis() / 1000L)
                .putString(MS_ITEM, GsonSingleton.getInstance().toJson(bjItemArrayList))
                .commit();
    }

    public static boolean isExpired(){
        long currentSecond = System.currentTimeMillis()/1000L;
        return currentSecond - getTimeStamp() > EXPIRY_TIME;
    }

    public static void setExpired(){
        getPrefInstance()
                .edit()
                .putLong(TIMESTAMP, 0L)
                .commit();
    }

    public static void clear(){
        getPrefInstance().edit().clear().apply();
    }
}
