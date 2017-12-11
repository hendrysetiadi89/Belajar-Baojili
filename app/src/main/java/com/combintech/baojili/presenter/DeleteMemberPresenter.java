package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.AddMemberInteractor;
import com.combintech.baojili.interactor.DeleteMemberInteractor;
import com.combintech.baojili.interactor.UpdateMemberInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class DeleteMemberPresenter extends BasePresenter {

    private DeleteMemberView deleteMemberView;
    public interface DeleteMemberView{
        void onErrorDeleteMember(Throwable throwable);
        void onSuccessDeleteMember(MessageResponse messageResponse);
    }
    public DeleteMemberPresenter(DeleteMemberView deleteMemberView){
        this.deleteMemberView = deleteMemberView;
    }

    public void deleteMember(String userName){
        Observable<MessageResponse> observable = new DeleteMemberInteractor(userName).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                deleteMemberView.onErrorDeleteMember(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                deleteMemberView.onSuccessDeleteMember(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
