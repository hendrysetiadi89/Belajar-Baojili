package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetStockListByLocInteractor;
import com.combintech.baojili.interactor.GetStockListInteractor;
import com.combintech.baojili.model.BJStock;
import com.combintech.baojili.model.StockByItem;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetStockListByLocPresenter extends BasePresenter {

    private GetStockListByLocView getStockListByLocView;
    public interface GetStockListByLocView{
        void onErrorGetStockList(Throwable throwable);
        void onSuccessGetStockList(ArrayList<StockByLocation> stockByLocationList);
    }
    public GetStockListByLocPresenter(GetStockListByLocView getStockListByLocView){
        this.getStockListByLocView = getStockListByLocView;
    }

    public void getStockListByLoc(){
        Observable<ArrayList<StockByLocation>> observable = new GetStockListByLocInteractor().execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<StockByLocation>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getStockListByLocView.onErrorGetStockList(e);
            }

            @Override
            public void onNext(ArrayList<StockByLocation> stockByLocationList) {
                Comparator<StockByLocation> comparator = new Comparator<StockByLocation>() {
                    @Override
                    public int compare(StockByLocation stockByLocation, StockByLocation t1) {
                        return stockByLocation.getLocationName().compareTo(t1.getLocationName());
                    }
                };
                Collections.sort(stockByLocationList, comparator);
                getStockListByLocView.onSuccessGetStockList(stockByLocationList);
            }
        });
        addSubscription(subscription);
    }
}
