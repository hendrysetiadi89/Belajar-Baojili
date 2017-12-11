package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.interactor.SignInInteractor;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class SignInPresenter extends BasePresenter {

    private SignInView signInView;
    public interface SignInView{
        void onErrorSignIn(Throwable throwable);
        void onSuccessSignIn();
    }
    public SignInPresenter(SignInView signInView){
        this.signInView = signInView;
    }

    public void signIn(String userId, String password){
        Observable<LoginResponse> observable = new SignInInteractor(userId,password).execute();

        Subscription subscription = observable.subscribe(new Observer<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                signInView.onErrorSignIn(e);
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                PreferenceLoginHelper.setPreference(loginResponse);
                signInView.onSuccessSignIn();
            }
        });
        addSubscription(subscription);
    }
}
