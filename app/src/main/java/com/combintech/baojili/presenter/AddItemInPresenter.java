package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.AddItemInInteractor;
import com.combintech.baojili.interactor.AddItemInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.exception.GeneralError;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class AddItemInPresenter extends BasePresenter {

    private AddItemInView addItemInView;
    public interface AddItemInView{
        void onErrorAddItemIn(Throwable throwable);
        void onSuccessAddItemIn(MessageResponse messageResponse);
    }
    public AddItemInPresenter(AddItemInView addItemInView){
        this.addItemInView = addItemInView;
    }

    public void addItemIn(final String locationId, final String sourceLocationId, String code, String size, final String inType,
                          final int quantity){
        Observable<ArrayList<BJItem>> getItemListObservable =
                new GetItemListInteractor("", size, code).execute();
        Subscription subscription = getItemListObservable.subscribe(new Observer<ArrayList<BJItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addItemInView.onErrorAddItemIn(e);
            }

            @Override
            public void onNext(ArrayList<BJItem> bjItemArrayList) {
                if (bjItemArrayList.size() == 1) {
                    String itemID = bjItemArrayList.get(0).getItemID();
                    addItemIn(locationId, sourceLocationId, itemID, inType, quantity);
                } else {
                    throw RetrofitException.dataError("Barang dengan kode dan ukuran tersebut tidak ditemukan");
                }
            }
        });
        addSubscription(subscription);
    }

    private void addItemIn(String locationId, String sourceLocationId, String itemId, String inType,
                          int quantity){
        Observable<MessageResponse> observable =
                new AddItemInInteractor(locationId, sourceLocationId, itemId, inType, quantity).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addItemInView.onErrorAddItemIn(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                addItemInView.onSuccessAddItemIn(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
