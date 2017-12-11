package com.combintech.baojili.activity.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.GoogleMapBaseActivity;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.location.AddLocationFragment;

public class AddLocationActivity extends GoogleMapBaseActivity
        implements AddLocationFragment.OnAddLocationFragmentListener {

    // this is monas, set default when the location is disabled.
    private static final double DEFAULT_LATITUDE = -6.175072;
    private static final double DEFAULT_LONGITUDE = 106.827142;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AddLocationActivity.class);
        activity.startActivityForResult(intent,RequestCode.ADD_LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(AddLocationFragment.newInstance(), false, AddLocationFragment.TAG);
        }
    }

    @Override
    protected double getInitialLatitude() {
        return DEFAULT_LATITUDE;
    }

    @Override
    protected double getInitialLongitude() {
        return DEFAULT_LONGITUDE;
    }

    // after open placepicker from google API
    @Override
    public void onLocationPicked(String placeId, String placeName, String placeAddress,
                                 double placeLat, double placeLng) {
        AddLocationFragment addLocationFragment = getAddLocationFragment();
        if (addLocationFragment!= null) {
            addLocationFragment.onLocationPicked(placeId, placeName, placeAddress, placeLat, placeLng);
        }
    }

    @Override
    public void onLocationMapMoved(double lat, double lng, String placeAddress) {
        AddLocationFragment addLocationFragment = getAddLocationFragment();
        if (addLocationFragment!= null) {
            addLocationFragment.onLocationMapMoved(lat, lng, placeAddress);
        }
    }

    @Override
    public void onCurrentLocationChanged(double lat, double lng, String placeAddress) {
        AddLocationFragment addLocationFragment = getAddLocationFragment();
        if (addLocationFragment!= null) {
            addLocationFragment.onCurrentLocationChanged(lat, lng, placeAddress);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getTitle());
    }

    private AddLocationFragment getAddLocationFragment() {
        return (AddLocationFragment) getSupportFragmentManager().findFragmentByTag(AddLocationFragment.TAG);
    }

    @Override
    public void onSuccessAddLocation() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
