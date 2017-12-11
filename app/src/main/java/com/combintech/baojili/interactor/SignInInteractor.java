package com.combintech.baojili.interactor;

import com.combintech.baojili.interactor.base.BaseInteractor;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class SignInInteractor extends BaseInteractor<LoginResponse> {
    private String userId;
    private String password;

    public SignInInteractor(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public Observable<LoginResponse> execute(){
        return RestClient
                .getClient()
                .login(userId, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
