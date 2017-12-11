package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.AddItemInInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.interactor.UpdateItemInInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdateItemInPresenter extends BasePresenter {

    private UpdateItemInView updateItemInView;
    public interface UpdateItemInView{
        void onErrorUpdateItemIn(Throwable throwable);
        void onSuccessUpdateItemIn(MessageResponse messageResponse);
    }
    public UpdateItemInPresenter(UpdateItemInView updateItemInView){
        this.updateItemInView = updateItemInView;
    }

    public void updateItemIn(final String trItemId,
                             final String locationId, final String sourceLocationId, String code, String size, final String inType,
                             final int quantity){
        Observable<ArrayList<BJItem>> getItemListObservable =
                new GetItemListInteractor("", size, code).execute();
        Subscription subscription = getItemListObservable.subscribe(new Observer<ArrayList<BJItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateItemInView.onErrorUpdateItemIn(e);
            }

            @Override
            public void onNext(ArrayList<BJItem> bjItemArrayList) {
                if (bjItemArrayList.size() == 1) {
                    String itemID = bjItemArrayList.get(0).getItemID();
                    updateItemIn(trItemId, locationId, sourceLocationId, itemID, inType, quantity);
                } else {
                    throw RetrofitException.dataError("Barang dengan kode dan ukuran tersebut tidak ditemukan");
                }
            }
        });
        addSubscription(subscription);
    }

    private void updateItemIn(String trItemId,
                             String locationId, String sourceLocationId, String itemId, String inType,
                          int quantity){
        Observable<MessageResponse> observable =
                new UpdateItemInInteractor(trItemId, locationId, sourceLocationId, itemId, inType, quantity).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateItemInView.onErrorUpdateItemIn(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                updateItemInView.onSuccessUpdateItemIn(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
