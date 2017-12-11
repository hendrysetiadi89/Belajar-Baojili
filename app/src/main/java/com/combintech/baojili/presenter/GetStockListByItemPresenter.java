package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetStockListByItemInteractor;
import com.combintech.baojili.interactor.GetStockListByLocInteractor;
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

public class GetStockListByItemPresenter extends BasePresenter {

    private GetStockListByItemView getStockListByItemView;
    public interface GetStockListByItemView{
        void onErrorGetStockListByItem(Throwable throwable);
        void onSuccessGetStockListByItem(ArrayList<StockByItem> stockByItemList);
    }
    public GetStockListByItemPresenter(GetStockListByItemView getStockListByItemView){
        this.getStockListByItemView = getStockListByItemView;
    }

    public void getStockListByItem(){
        Observable<ArrayList<StockByItem>> observable = new GetStockListByItemInteractor().execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<StockByItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getStockListByItemView.onErrorGetStockListByItem(e);
            }

            @Override
            public void onNext(ArrayList<StockByItem> stockByItemList) {
                Comparator<StockByItem> codeComparator = new Comparator<StockByItem>() {
                    @Override
                    public int compare(StockByItem stockByItem, StockByItem t1) {
                        int comparison = stockByItem.getCode().compareTo(t1.getCode());
                        if (comparison == 0){
                            comparison = stockByItem.getSize().compareTo(t1.getSize());
                        }
                        return comparison;
                    }
                };
                Collections.sort(stockByItemList, codeComparator);
                getStockListByItemView.onSuccessGetStockListByItem(stockByItemList);
            }
        });
        addSubscription(subscription);
    }
}
