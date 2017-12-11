package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class UpdateItemInteractor {
    private String itemId;
    private String description;
    private String type;
    private String code;
    private String size;
    private String price;
    private String cost;
    private String photoPath;

    public UpdateItemInteractor( String itemId,String description, String type, String code,
                                 String size, String price,
                                String cost, String photoPath) {
        this.itemId = itemId;
        this.description = description;
        this.type = type;
        this.code = code;
        this.size = size;
        this.price = price;
        this.cost = cost;
        this.photoPath = photoPath;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .updateItem(
                        RestClient.getRequestBodyFromUri(itemId),
                        RestClient.getRequestBodyFromUri(description),
                        RestClient.getRequestBodyFromUri(type),
                        RestClient.getRequestBodyFromUri(code),
                        RestClient.getRequestBodyFromUri(size),
                        RestClient.getRequestBodyFromUri(price),
                        RestClient.getRequestBodyFromUri(cost),
                        RestClient.getMultiPartBodyPartFromPath("photo", photoPath)
                )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
