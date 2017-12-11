package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;

import com.combintech.baojili.application.BaseApplication;
import com.combintech.baojili.application.GsonSingleton;
import com.combintech.baojili.model.BJLocation;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class PreferenceLocationHelper {
    private static final String PREF_NAME = "LocPref";

    private static final String LOCATION_ID = "loc_id";
    private static final String MAP_LOCATION_ID = "map_loc_id";
    private static final String MAP_LAT = "map_lat";
    private static final String MAP_LNG = "map_lng";
    private static final String MS_LOCATION = "ms_loc";
    private static final String TIMESTAMP = "timestamp";

    public static final int EXPIRY_TIME = 300; // 5 min

    private static SharedPreferences instance;

    private static SharedPreferences getPrefInstance(){
        if (instance == null) {
            instance = BaseApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static String getLocationId(){
        return getPrefInstance().getString(LOCATION_ID, "");
    }

    public static String getMapLocationId(){
        return getPrefInstance().getString(MAP_LOCATION_ID, "");
    }

    public static void getMapLatLng(float[] latlng){
        latlng[0] = getPrefInstance().getFloat(MAP_LAT, 0f);
        latlng[1] = getPrefInstance().getFloat(MAP_LNG, 0f);
    }

    public static long getTimeStamp(){
        return getPrefInstance().getLong(TIMESTAMP, 0);
    }

    public static void setLocationId(String locationId) {
        getPrefInstance()
                .edit()
                .putString(LOCATION_ID, locationId)
                .commit();
    }

    public static void setMapLocationId(String locationId) {
        getPrefInstance()
                .edit()
                .putString(MAP_LOCATION_ID, locationId)
                .commit();
    }

    public static void setMapLatLng(double lat, double lng) {
        getPrefInstance()
                .edit()
                .putFloat(MAP_LAT, (float)lat)
                .putFloat(MAP_LNG, (float)lng)
                .commit();
    }

    public static ArrayList<BJLocation> getMsLocation() {
        String jsonString = getPrefInstance().getString(MS_LOCATION, "");
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Type listType = new TypeToken< ArrayList<BJLocation> >(){}.getType();
        return GsonSingleton.getInstance().fromJson(jsonString, listType);
    }

    public static void setMsLocation(ArrayList<BJLocation> bjLocationArrayList){
        getPrefInstance()
                .edit()
                .putLong(TIMESTAMP, System.currentTimeMillis() / 1000L)
                .putString(MS_LOCATION, GsonSingleton.getInstance().toJson(bjLocationArrayList))
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
