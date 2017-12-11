package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.interactor.GetStockListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJStock;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetStockListPresenter extends BasePresenter {

    private GetStockListView getStockListView;
    public interface GetStockListView{
        void onErrorGetStockList(Throwable throwable);
        void onSuccessGetStockList(ArrayList<BJStock> bjStockList);
    }
    public GetStockListPresenter(GetStockListView getStockListView){
        this.getStockListView = getStockListView;
    }

    public void getStockList(String stockId, String locationId, String itemId){
        Observable<ArrayList<BJStock>> observable = new GetStockListInteractor(stockId, locationId, itemId).execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<BJStock>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getStockListView.onErrorGetStockList(e);
            }

            @Override
            public void onNext(ArrayList<BJStock> bjStockList) {
                getStockListView.onSuccessGetStockList(bjStockList);
            }
        });
        addSubscription(subscription);
    }
}
