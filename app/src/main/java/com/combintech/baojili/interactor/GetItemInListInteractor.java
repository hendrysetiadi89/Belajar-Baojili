package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetItemInListInteractor extends BaseInteractor<ArrayList<ItemInOut>> {

    private String locationId;
    private int start;
    private int row;
    private boolean isDeleted;
    public GetItemInListInteractor(String locationId, int start, int row, boolean isDeleted) {
        this.locationId = locationId;
        this.start = start;
        this.row = row;
        this.isDeleted = isDeleted;
    }

    public Observable<ArrayList<ItemInOut>> execute(){
        return RestClient
                .getClient()
                .getItemIn(locationId,
                        String.valueOf(start == 0?"":start),
                        String.valueOf(row == 0?"":row),
                        isDeleted? "1" : "0")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
