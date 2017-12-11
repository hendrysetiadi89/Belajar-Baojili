package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJStock;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetStockListByLocInteractor extends BaseInteractor<ArrayList<StockByLocation>> {

    public GetStockListByLocInteractor() {
    }

    public Observable<ArrayList<StockByLocation>> execute(){
        return RestClient
                .getClient()
                .getStockListByLoc()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
