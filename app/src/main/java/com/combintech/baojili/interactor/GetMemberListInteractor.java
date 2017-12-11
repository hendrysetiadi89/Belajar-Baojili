package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.model.User;
import com.combintech.baojili.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class GetMemberListInteractor extends BaseInteractor<ArrayList<User>> {

    public GetMemberListInteractor() {
    }

    public Observable<ArrayList<User>> execute(){
        return RestClient
                .getClient()
                .getMemberList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
