package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class DeleteItemInteractor {
    private String itemId;

    public DeleteItemInteractor(String itemId) {
        this.itemId = itemId;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .deleteItem(itemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
