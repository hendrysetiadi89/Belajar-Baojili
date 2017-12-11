package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.User;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetLocationListInteractor extends BaseInteractor<ArrayList<BJLocation>> {

    private String locationName;
    public GetLocationListInteractor(String locationName) {
        this.locationName = locationName;
    }

    public Observable<ArrayList<BJLocation>> execute(){
        return RestClient
                .getClient()
                .getLocationlist(locationName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
