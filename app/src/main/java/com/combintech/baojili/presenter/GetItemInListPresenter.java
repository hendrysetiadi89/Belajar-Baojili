package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemInListInteractor;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemInListPresenter extends BasePresenter {

    private GetItemInListView getItemInListView;
    public interface GetItemInListView{
        void onErrorGetItemInList(Throwable throwable);
        void onSuccessGetItemInList(ArrayList<ItemInOut> itemInOutList);
    }
    public GetItemInListPresenter(GetItemInListView getItemInListView){
        this.getItemInListView = getItemInListView;
    }

    public void getItemInList(String locationId, int start, int row, boolean isDeleted){
        Observable<ArrayList<ItemInOut>> observable = new GetItemInListInteractor(locationId, start, row, isDeleted).execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<ItemInOut>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getItemInListView.onErrorGetItemInList(e);
            }

            @Override
            public void onNext(ArrayList<ItemInOut> itemInOutList) {
                getItemInListView.onSuccessGetItemInList(itemInOutList);
            }
        });
        addSubscription(subscription);
    }
}
