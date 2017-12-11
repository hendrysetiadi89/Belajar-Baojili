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

public class GetItemListInteractor extends BaseInteractor<ArrayList<BJItem>> {

    private String itemID;
    private String size;
    private String code;
    public GetItemListInteractor(String itemID, String size, String code) {
        this.itemID = itemID;
        this.size = size;
        this.code = code;
    }

    public Observable<ArrayList<BJItem>> execute(){
        return RestClient
                .getClient()
                .getItemlist(itemID, size, code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
