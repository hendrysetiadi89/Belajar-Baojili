package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.interactor.UpdatePasswordInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.UpdatePasswordResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdatePasswordPresenter extends BasePresenter {

    private UpdatePasswordView updatePasswordView;
    public interface UpdatePasswordView{
        void onErrorUpdatePassword(Throwable throwable);
        void onSuccessUpdatePassword();
    }
    public UpdatePasswordPresenter(UpdatePasswordView updatePasswordView){
        this.updatePasswordView = updatePasswordView;
    }

    public void updatePassword(String userName, String oldPassword, String newPassword){
        Observable<UpdatePasswordResponse> observable = new UpdatePasswordInteractor(userName, oldPassword, newPassword).execute();

        Subscription subscription = observable.subscribe(new Observer<UpdatePasswordResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updatePasswordView.onErrorUpdatePassword(e);
            }

            @Override
            public void onNext(UpdatePasswordResponse updatePasswordResponse) {
                PreferenceLoginHelper.setClientKey(updatePasswordResponse.getAccessToken());
                updatePasswordView.onSuccessUpdatePassword();
            }
        });
        addSubscription(subscription);
    }
}
