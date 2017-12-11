package com.combintech.baojili.interactor;

import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.UpdatePasswordResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class UpdatePasswordInteractor {
    private String userName;
    private String password;
    private String oldPassword;

    public UpdatePasswordInteractor(String userName, String oldPassword, String password) {
        this.userName = userName;
        this.password = password;
        this.oldPassword = oldPassword;
    }

    public Observable<UpdatePasswordResponse> execute(){
        return RestClient
                .getClient()
                .updatePassword(userName, password, oldPassword)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
