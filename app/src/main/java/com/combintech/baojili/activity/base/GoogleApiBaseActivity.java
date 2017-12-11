package com.combintech.baojili.activity.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.combintech.baojili.R;
import com.combintech.baojili.bjinterface.PermissionCode;
import com.combintech.baojili.util.ErrorUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public abstract class GoogleApiBaseActivity extends PermissionBaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected final int ACTION_NONE = 0;
    protected final int ACTION_GETCURRENT_LOC = 1;
    protected final int ACTION_OPEN_PLACEPICKER = 2;

    public static final int LOC_INTERVAL = 120000;
    public static final int PLACE_PICKER_REQUEST = 167;

    private GoogleApiClient mGoogleApiClient;
    protected boolean googleApiConnSuccess = false;
    protected int googleApiActionCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiConnSuccess = true;
        if (googleApiActionCode == ACTION_GETCURRENT_LOC) {
            requestCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiConnSuccess = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiConnSuccess = false;
    }

    public void requestCurrentLocation(){
        googleApiActionCode = ACTION_GETCURRENT_LOC;
        if (! checkGoogleApiConnected()) return;
        if (isLocationEnabled()) {
            requestPermission(null, PermissionCode.LOCATION);
        } else {
            onLocationSettingDisabled();
        }
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void openPlacePicker(){
        if (! checkGoogleApiConnected()) return;
        googleApiActionCode = ACTION_OPEN_PLACEPICKER;
        requestPermission(null, PermissionCode.LOCATION);
    }

    private boolean checkGoogleApiConnected(){
        if (!googleApiConnSuccess) {
            return false;
        }
        return true;
    }

    @Override
    public void onPermissionGranted(@PermissionCode int permissionCode) {
        if (permissionCode == PermissionCode.LOCATION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            switch (googleApiActionCode) {
                case ACTION_GETCURRENT_LOC:
                    if (! checkGoogleApiConnected()) return;
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location == null) {
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(LOC_INTERVAL);
                        locationRequest.setFastestInterval(LOC_INTERVAL);

                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, locationRequest, GoogleApiBaseActivity.this);
                        // goTo onLocationChanged if success
                    } else {
                        onLocationChanged(location);
                    }
                    break;
                case ACTION_OPEN_PLACEPICKER:
                    try {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        Intent placePickerIntent = builder.build(GoogleApiBaseActivity.this);
                        startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST);
                        //goto onActivityResult
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            googleApiActionCode = ACTION_NONE;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (googleApiConnSuccess && mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public abstract void onLocationChanged(Location location);

    public void onLocationSettingDisabled(){
        ErrorUtil.showToast(this, getString(R.string.please_enable_location_feature));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(this,data);
            if (place == null) {
                Log.i("Test", "No Place selected");
            } else { // place is selected
                String placeId = place.getId();
                String placeName = place.getName() == null? null: place.getName().toString();
                String placeAddress =place.getAddress() == null? null: place.getAddress().toString();
                double placeLat = place.getLatLng().latitude;
                double placeLng = place.getLatLng().longitude;

                onLocationPicked(placeId, placeName, placeAddress, placeLat, placeLng);

                // If you know the ID, and want to retrieve the detail:
                /*PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(mClient, placeId);
                placeBufferPendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        Log.i("Test", places.toString());
                    }
                });*/
            }
        }
    }

    public abstract void onLocationPicked(String placeId, String placeName,
                                          String palceAddress, double palceLat, double placeLng);
}
