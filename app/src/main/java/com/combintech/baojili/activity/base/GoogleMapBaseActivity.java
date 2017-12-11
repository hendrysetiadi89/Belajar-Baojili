package com.combintech.baojili.activity.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.combintech.baojili.bjinterface.PermissionCode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hendry Setiadi
 */

public abstract class GoogleMapBaseActivity extends GoogleApiBaseActivity implements OnMapReadyCallback {

    public static final String SAVED_LAT = "svd_lat";
    public static final String SAVED_LON = "svd_lon";
    public static final String SAVED_PLACE_ADDRESS = "svd_place_address";

    private SupportMapFragment mapFrag;
    private GoogleMap mGoogleMap;

    private double lat;
    private double lng;
    private String placeAddress;

    private Marker mCurrLocationMarker;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            lat = getInitialLatitude();
            lng = getInitialLongitude();
            placeAddress = getInitialPlaceAddress();
        } else {
            lat = savedInstanceState.getDouble(SAVED_LAT, 0);
            lng = savedInstanceState.getDouble(SAVED_LON, 0);
            placeAddress = savedInstanceState.getString(SAVED_PLACE_ADDRESS, "");
        }
    }

    public final void setMapAsync(SupportMapFragment supportMapFragment){
        mapFrag = supportMapFragment;
        mapFrag.getMapAsync(this);
    }

    protected double getInitialLatitude(){
        return 0f;
    }

    protected double getInitialLongitude(){
        return 0f;
    }

    protected String getInitialPlaceAddress(){
        return "";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mCurrLocationMarker.setPosition(latLng);
                lat = latLng.latitude;
                lng = latLng.longitude;

                requestPlaceAddress(lat, lng);

                onLocationMapMoved(lat, lng, placeAddress);
            }
        });

        // we make sure the permission has been granted when we setlocation enabled
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        if (googleApiConnSuccess) {
            onGoogleConnectedAndMapSet();
        }
    }

    // automatically call currentloc after connected to google.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        if (mGoogleMap!=null) {
            onGoogleConnectedAndMapSet();
        }
    }

    protected void onGoogleConnectedAndMapSet(){
        if (hasInitialLocationSet()) {
            moveMarker(lat, lng);
            onLocationMapMoved(lat, lng, getInitialPlaceAddress());
        } else {
            requestCurrentLocation();
        }
    }

    protected boolean hasInitialLocationSet(){
        return false;
    }

    @Override
    public void onLocationSettingDisabled() {
        super.onLocationSettingDisabled();
        if (lat!= 0 && lng != 0){
            onLocationChanged(lat, lng);
        }
    }

    @Override
    public void onPermissionDenied(@PermissionCode int permissionCode) {
        super.onPermissionDenied(permissionCode);
        if (lat!= 0 && lng != 0){
            onLocationChanged(lat, lng);
        }
    }

    @Override
    public final void onLocationChanged(Location location) {
        onLocationChanged(location.getLatitude(), location.getLongitude());
    }

    public final void onLocationChanged(double lat, double lon){
        this.lat = lat;
        this.lng = lon;

        if (mGoogleMap==null || ! googleApiConnSuccess) {
            return;
        }

        requestPlaceAddress(lat, lon);
        moveMarker(lat, lon);

        onCurrentLocationChanged(lat, lon, placeAddress);
    }

    private void moveMarker(double lat, double lon) {
        if (mCurrLocationMarker!=null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getMarkerTitleString());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    private void requestPlaceAddress(double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses == null || addresses.size() == 0) {
                placeAddress = "lat:" + lat + ";lng:" + lng;
                return;
            }
            placeAddress = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMarkerTitleString(){
        return "";
    }

    @Override
    public abstract void onLocationPicked(String placeId, String placeName, String palceAddress, double palceLat, double placeLng);

    public abstract void onLocationMapMoved(double lat, double lng, String placeAddress);

    public abstract void onCurrentLocationChanged(double lat, double lng, String placeAddress);

    public LatLng getLatLonFromMap() {
        return new LatLng(lat, lng);
    }

    public String getAddressFromMap(){
        return this.placeAddress;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(SAVED_LAT, lat);
        outState.putDouble(SAVED_LON, lng);
        outState.putString(SAVED_PLACE_ADDRESS, placeAddress);
    }
}
