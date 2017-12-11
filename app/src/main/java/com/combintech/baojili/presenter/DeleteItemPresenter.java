package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceMsItemHelper;
import com.combintech.baojili.interactor.DeleteItemInteractor;
import com.combintech.baojili.interactor.DeleteLocationInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class DeleteItemPresenter extends BasePresenter {

    private DeleteItemView deleteItemView;
    public interface DeleteItemView{
        void onErrorDeleteItem(Throwable throwable);
        void onSuccessDeleteItem(MessageResponse messageResponse);
    }
    public DeleteItemPresenter(DeleteItemView deleteItemView){
        this.deleteItemView = deleteItemView;
    }

    public void deleteItem(String itemId){
        Observable<MessageResponse> observable = new DeleteItemInteractor(itemId).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                deleteItemView.onErrorDeleteItem(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceMsItemHelper.setExpired();
                deleteItemView.onSuccessDeleteItem(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
