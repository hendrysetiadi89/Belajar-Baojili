package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.interactor.AddMemberInteractor;
import com.combintech.baojili.interactor.SignInInteractor;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class AddMemberPresenter extends BasePresenter {

    private AddMemberView addMemberView;
    public interface AddMemberView{
        void onErrorAddMember(Throwable throwable);
        void onSuccessAddMember(MessageResponse messageResponse);
    }
    public AddMemberPresenter(AddMemberView addMemberView){
        this.addMemberView = addMemberView;
    }

    public void addMember(String userName, String name, String email, String password, String role,
                          String photoPath){
        Observable<MessageResponse> observable = new AddMemberInteractor(userName, name, email, password, role, photoPath).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addMemberView.onErrorAddMember(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                addMemberView.onSuccessAddMember(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
