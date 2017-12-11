package com.combintech.baojili.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.HomeActivity;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.fragment.auth.SigninFragment;

public class AuthActivity extends BaseActivity implements SigninFragment.OnSignInFragmentListener{

    public static void start(Context context){
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
    }

    public static void startNewTask(Context context){
        Intent intent = new Intent(context, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment_no_toolbar);

        if (savedInstanceState == null) {
            replaceFragment(new SigninFragment(), false);
        }
    }

    @Override
    public void onSignInResponseSuccess() {
        HomeActivity.start(this);
        finish();
    }
}
