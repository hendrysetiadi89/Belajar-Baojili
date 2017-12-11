package com.combintech.baojili.activity.location;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.GoogleApiBaseActivity;
import com.combintech.baojili.activity.base.GoogleMapBaseActivity;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.location.UpdateLocationFragment;
import com.combintech.baojili.model.BJLocation;

public class UpdateLocationActivity extends GoogleMapBaseActivity
    implements UpdateLocationFragment.OnUpdateLocationFragmentListener{

    public static final String EXTRA_LOCATION = "x_location";

    private BJLocation bjLocation;

    public static void start(Activity activity, BJLocation bjLocation) {
        Intent intent = new Intent(activity, UpdateLocationActivity.class);
        intent.putExtra(EXTRA_LOCATION, bjLocation);
        activity.startActivityForResult(intent,RequestCode.UPDATE_LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bjLocation = getIntent().getParcelableExtra(EXTRA_LOCATION);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(UpdateLocationFragment.newInstance(), false, UpdateLocationFragment.TAG);
        }
    }

    @Override
    protected boolean hasInitialLocationSet() {
        return true;
    }

    @Override
    protected double getInitialLatitude() {
        return Double.parseDouble(bjLocation.getLatitude());
    }

    @Override
    protected double getInitialLongitude() {
        return Double.parseDouble(bjLocation.getLongitude());
    }

    @Override
    protected String getInitialPlaceAddress() {
        return bjLocation.getAddress();
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.update_location) + " "+ bjLocation.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private UpdateLocationFragment getUpdateLocationFragment() {
        return (UpdateLocationFragment) getSupportFragmentManager().findFragmentByTag(UpdateLocationFragment.TAG);
    }

    @Override
    public void onLocationPicked(String placeId, String placeName, String placeAddress, double placeLat, double placeLng) {
        UpdateLocationFragment updateLocationFragment = getUpdateLocationFragment();
        if (updateLocationFragment!= null) {
            updateLocationFragment.onLocationPicked(placeId, placeName, placeAddress, placeLat, placeLng);
        }
    }

    @Override
    public void onLocationMapMoved(double lat, double lng, String placeAddress) {
        UpdateLocationFragment updateLocationFragment = getUpdateLocationFragment();
        if (updateLocationFragment!= null) {
            updateLocationFragment.onLocationMapMoved(lat, lng, placeAddress);
        }
    }

    @Override
    public void onCurrentLocationChanged(double lat, double lng, String placeAddress) {
        UpdateLocationFragment updateLocationFragment = getUpdateLocationFragment();
        if (updateLocationFragment!= null) {
            updateLocationFragment.onCurrentLocationChanged(lat, lng, placeAddress);
        }
    }

    @Override
    public void onSuccessUpdateLocation() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
