package com.combintech.baojili.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.combintech.baojili.activity.auth.AuthActivity;
import com.combintech.baojili.activity.base.GoogleMapBaseActivity;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if (PreferenceLoginHelper.isLoggedIn()) {
            HomeActivity.start(this);
        }
        else {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        }
        this.finish();
    }
}
