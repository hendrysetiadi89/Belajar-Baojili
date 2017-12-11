package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class UpdateItemInInteractor {
    private String trItemId;
    private String locationId;
    private String sourceLocationId;
    private String itemId;
    private String inType;
    private int quantity;

    public UpdateItemInInteractor(String trItemId,
                                  String locationId, String sourceLocationId, String itemId, String inType,
                                  int quantity) {
        this.trItemId = trItemId;
        this.locationId = locationId;
        this.sourceLocationId = sourceLocationId;
        this.itemId = itemId;
        this.inType = inType;
        this.quantity = quantity;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .updateItemIn(trItemId, locationId, sourceLocationId, itemId, inType,
                        quantity==0?"":String.valueOf(quantity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
