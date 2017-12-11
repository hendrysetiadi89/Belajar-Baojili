package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class UpdateItemOutInteractor {
    private String trItemId;
    private String locationId;
    private String targetLocationId;
    private String itemId;
    private String outType;
    private int quantity;

    public UpdateItemOutInteractor(String trItemId,
                                   String locationId, String targetLocationId, String itemId, String outType,
                                   int quantity) {
        this.trItemId = trItemId;
        this.locationId = locationId;
        this.targetLocationId = targetLocationId;
        this.itemId = itemId;
        this.outType = outType;
        this.quantity = quantity;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .updateItemOut(trItemId, locationId, targetLocationId, itemId, outType,
                        quantity==0?"":String.valueOf(quantity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
