package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.AddItemInInteractor;
import com.combintech.baojili.interactor.AddItemOutInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class AddItemOutPresenter extends BasePresenter {

    private AddItemOutView addItemOutView;
    public interface AddItemOutView{
        void onErrorAddItemOut(Throwable throwable);
        void onSuccessAddItemOut(MessageResponse messageResponse);
    }
    public AddItemOutPresenter(AddItemOutView addItemOutView){
        this.addItemOutView = addItemOutView;
    }

    public void addItemOut(final String locationId, final String targetLocationId, String code, String size, final String inType,
                          final int quantity){
        Observable<ArrayList<BJItem>> getItemListObservable =
                new GetItemListInteractor("", size, code).execute();
        Subscription subscription = getItemListObservable.subscribe(new Observer<ArrayList<BJItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addItemOutView.onErrorAddItemOut(e);
            }

            @Override
            public void onNext(ArrayList<BJItem> bjItemArrayList) {
                if (bjItemArrayList.size() == 1) {
                    String itemID = bjItemArrayList.get(0).getItemID();
                    addItemOut(locationId, targetLocationId, itemID, inType, quantity);
                } else {
                    throw RetrofitException.dataError("Barang dengan kode dan ukuran tersebut tidak ditemukan");
                }
            }
        });
        addSubscription(subscription);
    }

    private void addItemOut(String locationId, String targetLocationId, String itemId, String inType,
                          int quantity){
        Observable<MessageResponse> observable =
                new AddItemOutInteractor(locationId, targetLocationId, itemId, inType, quantity).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addItemOutView.onErrorAddItemOut(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                addItemOutView.onSuccessAddItemOut(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
