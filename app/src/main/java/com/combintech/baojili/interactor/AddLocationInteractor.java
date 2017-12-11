package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class AddLocationInteractor {
    private String name;
    private String type;
    private String address;
    private String latitude;
    private String longitude;

    public AddLocationInteractor(String name, String type, String address, String latitude, String longitude) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .addLocation(name, type, address, latitude, longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
