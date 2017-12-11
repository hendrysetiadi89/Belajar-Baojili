package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJStock;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetStockListInteractor extends BaseInteractor<ArrayList<BJStock>> {

    private String stockId;
    private String locationId;
    private String itemId;
    public GetStockListInteractor(String stockId, String locationId, String itemId) {
        this.stockId = stockId;
        this.locationId = locationId;
        this.itemId = itemId;
    }

    public Observable<ArrayList<BJStock>> execute(){
        return RestClient
                .getClient()
                .getStockList(stockId, locationId, itemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
