package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJTrHistory;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetItemHistoryListInteractor extends BaseInteractor<ArrayList<BJTrHistory>> {

    private String trItemId;
    public GetItemHistoryListInteractor(String trItemId) {
        this.trItemId = trItemId;
    }

    public Observable<ArrayList<BJTrHistory>> execute(){
        return RestClient
                .getClient()
                .itemHistory(trItemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
