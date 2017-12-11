package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class DeleteMemberInteractor {
    private String userName;

    public DeleteMemberInteractor(String userName) {
        this.userName = userName;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .deleteMember(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
