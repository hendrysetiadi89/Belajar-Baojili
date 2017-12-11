package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.CodeAndSizeList;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetCodeAndSizeListInteractor extends BaseInteractor<CodeAndSizeList> {

    public GetCodeAndSizeListInteractor() {
    }

    public Observable<CodeAndSizeList> execute(){
        return RestClient
                .getClient()
                .getCodeAndSizeList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
