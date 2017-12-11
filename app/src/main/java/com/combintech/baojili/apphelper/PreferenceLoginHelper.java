package com.combintech.baojili.apphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.combintech.baojili.application.BaseApplication;
import com.combintech.baojili.model.LoginResponse;

public class PreferenceLoginHelper {

    private static final String PREF_NAME = "BaojiliPref";
    private static final String CLIENT_KEY = "CLIENT_KEY";

    private static final String USER_ID = "USER_ID";
    private static final String NAME = "NAME";
    private static final String EMAIL = "EMAIL";
    private static final String PHOTO = "PHOTO";
    private static final String ROLE = "ROLE";

    private static SharedPreferences instance;

    private static SharedPreferences getPrefInstance(){
        if (instance == null) {
            instance = BaseApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static boolean isLoggedIn(){
        return !TextUtils.isEmpty(getClientKey());
    }

    public static void setPreference(LoginResponse loginResponse){
        getPrefInstance()
                .edit()
                .putString(USER_ID, loginResponse.getUserId())
                .putString(NAME, loginResponse.getName())
                .putString(EMAIL, loginResponse.getEmail())
                .putString(ROLE, loginResponse.getRole())
                .putString(CLIENT_KEY, loginResponse.getAccessToken())
                .putString(PHOTO, loginResponse.getPhoto())
                .commit();
    }

    public static String getClientKey(){
        return getPrefInstance().getString(CLIENT_KEY, "");
    }

    public static void setClientKey(String clientKey) {
        getPrefInstance()
                .edit()
                .putString(CLIENT_KEY, clientKey)
                .commit();
    }

    public static String getName(){
        return getPrefInstance().getString(NAME, "");
    }

    public static String getRole(){
        return getPrefInstance().getString(ROLE, "");
    }

    public static String getUserId(){
        return getPrefInstance().getString(USER_ID, "");
    }
    public static String getEmail(){
        return getPrefInstance().getString(EMAIL, "");
    }
    public static String getPhoto(){
        return getPrefInstance().getString(PHOTO, "");
    }

    public static void logOut(){
        getPrefInstance().edit().clear().apply();
    }

}
