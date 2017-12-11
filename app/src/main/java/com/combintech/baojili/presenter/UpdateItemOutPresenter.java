package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.interactor.UpdateItemOutInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdateItemOutPresenter extends BasePresenter {

    private UpdateItemOutView updateItemOutView;
    public interface UpdateItemOutView{
        void onErrorUpdateItemOut(Throwable throwable);
        void onSuccessUpdateItemOut(MessageResponse messageResponse);
    }
    public UpdateItemOutPresenter(UpdateItemOutView updateItemOutView){
        this.updateItemOutView = updateItemOutView;
    }

    public void updateItemOut(final String trItemId,
                              final String locationId, final String targetLocationId, String code, String size, final String inType,
                              final int quantity){
        Observable<ArrayList<BJItem>> getItemListObservable =
                new GetItemListInteractor("", size, code).execute();
        Subscription subscription = getItemListObservable.subscribe(new Observer<ArrayList<BJItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateItemOutView.onErrorUpdateItemOut(e);
            }

            @Override
            public void onNext(ArrayList<BJItem> bjItemArrayList) {
                if (bjItemArrayList.size() == 1) {
                    String itemID = bjItemArrayList.get(0).getItemID();
                    updateItemOut(trItemId, locationId, targetLocationId, itemID, inType, quantity);
                } else {
                    throw RetrofitException.dataError("Barang dengan kode dan ukuran tersebut tidak ditemukan");
                }
            }
        });
        addSubscription(subscription);
    }

    private void updateItemOut(String trItemId,
                              String locationId, String targetLocationId, String itemId, String inType,
                          int quantity){
        Observable<MessageResponse> observable =
                new UpdateItemOutInteractor(trItemId,locationId, targetLocationId, itemId, inType, quantity).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateItemOutView.onErrorUpdateItemOut(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                updateItemOutView.onSuccessUpdateItemOut(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
