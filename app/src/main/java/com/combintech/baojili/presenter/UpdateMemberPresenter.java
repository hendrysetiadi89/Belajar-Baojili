package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.UpdateMemberInteractor;
import com.combintech.baojili.interactor.UpdatePasswordInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;
import com.combintech.baojili.util.ImageHelper;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdateMemberPresenter extends BasePresenter {

    private UpdateMemberView updateMemberView;
    public interface UpdateMemberView{
        void onErrorUpdateMember(Throwable throwable);
        void onSuccessUpdateMember(MessageResponse messageResponse);
    }
    public UpdateMemberPresenter(UpdateMemberView updateMemberView){
        this.updateMemberView = updateMemberView;
    }

    public void updateMember(String username, String name, String email, String role, String photoPath){
        Observable<MessageResponse> observable = new UpdateMemberInteractor(username, name, email, role, photoPath).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateMemberView.onErrorUpdateMember(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                updateMemberView.onSuccessUpdateMember(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
