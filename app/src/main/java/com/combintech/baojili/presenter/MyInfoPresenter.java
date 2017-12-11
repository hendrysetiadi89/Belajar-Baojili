package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.interactor.MyInfoInteractor;
import com.combintech.baojili.interactor.SignInInteractor;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class MyInfoPresenter extends BasePresenter {

    private GetMyInfoView getMyInfoView;
    public interface GetMyInfoView{
        void onErrorGetMyInfo(Throwable throwable);
        void onSuccessGetMyInfo();
    }
    public MyInfoPresenter(GetMyInfoView getMyInfoView){
        this.getMyInfoView = getMyInfoView;
    }

    public void getMyInfo(){
        Observable<LoginResponse> observable = new MyInfoInteractor().execute();

        Subscription subscription = observable.subscribe(new Observer<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getMyInfoView.onErrorGetMyInfo(e);
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                PreferenceLoginHelper.setPreference(loginResponse);
                getMyInfoView.onSuccessGetMyInfo();
            }
        });
        addSubscription(subscription);
    }
}
