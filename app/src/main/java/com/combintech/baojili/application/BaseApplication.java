package com.combintech.baojili.application;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 *
 */

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // Fabric.with(this, new Crashlytics());

        mContext = getApplicationContext();

        Stetho.initializeWithDefaults(this);

//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
//        Realm.setDefaultConfiguration(realmConfiguration);

//        OneSignal.startInit(this).init();

        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);
    }

    public static Context getContext(){
        return mContext;
    }
}
