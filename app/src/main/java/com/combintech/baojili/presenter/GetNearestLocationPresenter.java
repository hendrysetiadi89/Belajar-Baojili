package com.combintech.baojili.presenter;

import android.location.Location;
import android.text.TextUtils;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.GetLocationListInteractor;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetNearestLocationPresenter extends BasePresenter {

    public static final int THRES_SAME_LOC = 300; //meters
    private GetNearestLocationView getNearestLocationView;

    public interface GetNearestLocationView {
        void onErrorGetNearestLocationView(Throwable throwable);

        void onSuccessGetNearestLocationView(BJLocation bjLocation);
    }

    public GetNearestLocationPresenter(GetNearestLocationView getNearestLocationView) {
        this.getNearestLocationView = getNearestLocationView;
    }

    public void getNearestLocation(final double lat, final double lng) {
        if (PreferenceLocationHelper.isExpired()) {
            Observable<ArrayList<BJLocation>> observable = new GetLocationListInteractor("").execute();

            Subscription subscription = observable.subscribe(new Observer<ArrayList<BJLocation>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    getNearestLocationView.onErrorGetNearestLocationView(e);
                }

                @Override
                public void onNext(ArrayList<BJLocation> bjLocationList) {
                    PreferenceLocationHelper.setMsLocation(bjLocationList);
                    searchNearestLocation(lat, lng, bjLocationList);
                }
            });
            addSubscription(subscription);
        } else {
            searchNearestLocation(lat, lng, PreferenceLocationHelper.getMsLocation());
        }

    }

    private void searchNearestLocation(double lat, double lng, ArrayList<BJLocation> bjLocationArrayList) {
        if (bjLocationArrayList == null || bjLocationArrayList.size() == 0) {
            getNearestLocationView.onErrorGetNearestLocationView(RetrofitException.dataError("Tidak menemukan lokasi. Silakan coba lagi"));
            return;
        }
        float previousLatLng[] = new float[2];
        PreferenceLocationHelper.getMapLatLng(previousLatLng);
        Location prevLocation = new Location("P");
        prevLocation.setLatitude(previousLatLng[0]);
        prevLocation.setLongitude(previousLatLng[1]);

        Location newLocation = new Location("N");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);

        // if the newlocation is same with the stored one, just return the stored one.
        if (newLocation.distanceTo(prevLocation) <= THRES_SAME_LOC) {
            String prevMapLocId = PreferenceLocationHelper.getMapLocationId();
            if (!TextUtils.isEmpty(prevMapLocId) ){
                for (int i = 0, sizei = bjLocationArrayList.size(); i<sizei; i++) {
                    BJLocation bjLocation = bjLocationArrayList.get(i);
                    if (prevMapLocId.equals(bjLocation.getLocationID())) {
                        getNearestLocationView.onSuccessGetNearestLocationView(bjLocation);
                        return;
                    }
                }
            }
        }

        BJLocation nearestBJLocation = null;
        float smallestDistanceInMeter = Float.MAX_VALUE;

        // the location is different
        for (int i = 0, sizei = bjLocationArrayList.size(); i<sizei; i++) {
            BJLocation bjLocation = bjLocationArrayList.get(i);
            double sourceLat = Double.parseDouble(bjLocation.getLatitude());
            double sourceLng = Double.parseDouble(bjLocation.getLongitude());
            if (sourceLat == 0 || sourceLng == 0) { // no need to search location that has not set.
                continue;
            }
            Location location = new Location("I");
            location.setLatitude(sourceLat);
            location.setLongitude(sourceLng);
            float distance = location.distanceTo(newLocation);
            if (distance <= THRES_SAME_LOC) {
                PreferenceLocationHelper.setMapLocationId(bjLocation.getLocationID());
                PreferenceLocationHelper.setMapLatLng(lat, lng);
                getNearestLocationView.onSuccessGetNearestLocationView(bjLocation);
                return;
            }
            //store the minimum when looping
            if (distance <= smallestDistanceInMeter) {
                nearestBJLocation = bjLocation;
                smallestDistanceInMeter = distance;
            }

        }

        // just return index 0 if not found
        if (nearestBJLocation == null) {
            getNearestLocationView.onSuccessGetNearestLocationView(bjLocationArrayList.get(0));
        } else {
            PreferenceLocationHelper.setMapLocationId(nearestBJLocation.getLocationID());
            PreferenceLocationHelper.setMapLatLng(lat, lng);
            getNearestLocationView.onSuccessGetNearestLocationView(nearestBJLocation);
        }
    }

}
