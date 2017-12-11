package com.combintech.baojili.interactor;

import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.RestClient;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hendry Setiadi
 */

public class AddMemberInteractor {
    private String userName;
    private String name;
    private String email;
    private String password;
    private String role;
    private String photoPath;

    public AddMemberInteractor(String userName, String name, String email, String password, String role,
            String photoPath) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.photoPath = photoPath;
    }

    public Observable<MessageResponse> execute(){
        return RestClient
                .getClient()
                .addMember(
                        RestClient.getRequestBodyFromUri(userName),
                        RestClient.getRequestBodyFromUri(name),
                        RestClient.getRequestBodyFromUri(email),
                        RestClient.getRequestBodyFromUri(password),
                        RestClient.getRequestBodyFromUri(role),
                        RestClient.getMultiPartBodyPartFromPath("photo", photoPath)
                )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
