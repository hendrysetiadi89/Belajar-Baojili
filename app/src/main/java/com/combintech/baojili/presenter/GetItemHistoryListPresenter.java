package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemHistoryListInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJTrHistory;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemHistoryListPresenter extends BasePresenter {

    private GetItemHistoryListView getItemHistoryListView;
    public interface GetItemHistoryListView{
        void onErrorGetItemHistoryList(Throwable throwable);
        void onSuccessGetItemHistoryList(ArrayList<BJTrHistory> bjTrHistoryList);
    }
    public GetItemHistoryListPresenter(GetItemHistoryListView getItemHistoryListView){
        this.getItemHistoryListView = getItemHistoryListView;
    }

    public void getItemHistoryList(String trItemId){
        Observable<ArrayList<BJTrHistory>> observable = new GetItemHistoryListInteractor(trItemId).execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<BJTrHistory>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getItemHistoryListView.onErrorGetItemHistoryList(e);
            }

            @Override
            public void onNext(ArrayList<BJTrHistory> bjTrHistoryList) {
                getItemHistoryListView.onSuccessGetItemHistoryList(bjTrHistoryList);
            }
        });
        addSubscription(subscription);
    }
}
