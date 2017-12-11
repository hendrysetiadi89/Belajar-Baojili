package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetItemListByLocInteractor extends BaseInteractor<ArrayList<BJItem>> {

    private String locationId;
    private String size;
    private String code;
    public GetItemListByLocInteractor(String locationId, String size, String code) {
        this.locationId = locationId;
        this.size = size;
        this.code = code;
    }

    public Observable<ArrayList<BJItem>> execute(){
        return RestClient
                .getClient()
                .getItemByLocation(locationId, size, code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
