package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceMsItemHelper;
import com.combintech.baojili.interactor.AddItemInteractor;
import com.combintech.baojili.interactor.UpdateItemInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdateItemPresenter extends BasePresenter {

    private UpdateItemView updateItemView;
    public interface UpdateItemView{
        void onErrorUpdateItem(Throwable throwable);
        void onSuccessUpdateItem(MessageResponse messageResponse);
    }
    public UpdateItemPresenter(UpdateItemView updateItemView){
        this.updateItemView = updateItemView;
    }

    public void updateItem(String itemId, String description, String type,
                           String code, String size, String price,
                          String cost, String photoPath){
        Observable<MessageResponse> observable =
                new UpdateItemInteractor(itemId, description,type, code,size,price, cost, photoPath).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateItemView.onErrorUpdateItem(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceMsItemHelper.setExpired();
                updateItemView.onSuccessUpdateItem(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
