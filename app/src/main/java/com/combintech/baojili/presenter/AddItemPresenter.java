package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceMsItemHelper;
import com.combintech.baojili.interactor.AddItemInteractor;
import com.combintech.baojili.interactor.AddMemberInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class AddItemPresenter extends BasePresenter {

    private AddItemView addItemView;
    public interface AddItemView{
        void onErrorAddItem(Throwable throwable);
        void onSuccessAddItem(MessageResponse messageResponse);
    }
    public AddItemPresenter(AddItemView addItemView){
        this.addItemView = addItemView;
    }

    public void addItem(String description, String type, String code, String size, String price,
                          String cost, String photoPath){
        Observable<MessageResponse> observable =
                new AddItemInteractor(description,type, code,size,price, cost, photoPath).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addItemView.onErrorAddItem(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceMsItemHelper.setExpired();
                addItemView.onSuccessAddItem(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
