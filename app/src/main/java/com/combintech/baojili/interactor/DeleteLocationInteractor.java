package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class DeleteLocationInteractor {
    private String locationId;

    public DeleteLocationInteractor(String locationId) {
        this.locationId = locationId;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .deleteLocation(locationId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
