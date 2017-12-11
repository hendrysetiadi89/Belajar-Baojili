package com.combintech.baojili.activity.itemout;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.GoogleApiBaseActivity;
import com.combintech.baojili.apphelper.PreferenceReminderHelper;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.itemin.AddItemInFragment;
import com.combintech.baojili.fragment.itemout.AddItemOutFragment;

public class AddItemOutActivity extends GoogleApiBaseActivity
        implements AddItemOutFragment.OnAddItemOutFragmentListener{

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AddItemOutActivity.class);
        activity.startActivityForResult(intent,RequestCode.ADD_ITEM_OUT_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(AddItemOutFragment.newInstance(), false, AddItemOutFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getTitle());
    }

    @Override
    public void onLocationPicked(String placeId, String placeName, String palceAddress, double palceLat, double placeLng) {

    }

    @Override
    public void onLocationChanged(Location location) {
        AddItemOutFragment addItemOutFragment = getAddItemOutFragment();
        if (addItemOutFragment != null) {
            addItemOutFragment.onLocationChanged(location);
        }
    }

    private AddItemOutFragment getAddItemOutFragment() {
        return (AddItemOutFragment) getSupportFragmentManager().findFragmentByTag(AddItemOutFragment.TAG);
    }

    @Override
    public void onSuccessAddItemOut() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
