package com.combintech.baojili.activity.itemin;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.activity.base.GoogleApiBaseActivity;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.apphelper.PreferenceReminderHelper;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.itemin.AddItemInFragment;

public class AddItemInActivity extends GoogleApiBaseActivity
        implements AddItemInFragment.OnAddItemInFragmentListener{

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AddItemInActivity.class);
        activity.startActivityForResult(intent,RequestCode.ADD_ITEM_IN_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(AddItemInFragment.newInstance(), false, AddItemInFragment.TAG);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        AddItemInFragment addItemInFragment = getAddItemInFragment();
        if (addItemInFragment != null) {
            addItemInFragment.onLocationChanged(location);
        }
    }

    @Override
    public void onLocationPicked(String placeId, String placeName, String palceAddress, double palceLat, double placeLng) {

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getTitle());
    }

    private AddItemInFragment getAddItemInFragment() {
        return (AddItemInFragment) getSupportFragmentManager().findFragmentByTag(AddItemInFragment.TAG);
    }

    @Override
    public void onSuccessAddItemIn() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
