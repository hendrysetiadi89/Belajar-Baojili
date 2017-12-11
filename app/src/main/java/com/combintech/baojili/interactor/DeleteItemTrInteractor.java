package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class DeleteItemTrInteractor {
    private String itemTrId;

    public DeleteItemTrInteractor(String itemTrId) {
        this.itemTrId = itemTrId;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .deleteItemTr(itemTrId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
