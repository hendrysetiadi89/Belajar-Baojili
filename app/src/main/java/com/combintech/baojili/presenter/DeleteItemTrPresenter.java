package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.DeleteItemInteractor;
import com.combintech.baojili.interactor.DeleteItemTrInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class DeleteItemTrPresenter extends BasePresenter {

    private DeleteItemTrView deleteItemTrView;
    public interface DeleteItemTrView{
        void onErrorDeleteItemTr(Throwable throwable);
        void onSuccessDeleteItemTr(MessageResponse messageResponse);
    }
    public DeleteItemTrPresenter(DeleteItemTrView deleteItemTrView){
        this.deleteItemTrView = deleteItemTrView;
    }

    public void deleteItemTr(String itemTrId){
        Observable<MessageResponse> observable = new DeleteItemTrInteractor(itemTrId).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                deleteItemTrView.onErrorDeleteItemTr(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                deleteItemTrView.onSuccessDeleteItemTr(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
