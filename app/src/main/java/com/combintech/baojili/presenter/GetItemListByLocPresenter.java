package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemListByLocInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemListByLocPresenter extends BasePresenter {

    private GetItemListByLocView getItemListByLocView;
    public interface GetItemListByLocView{
        void onErrorGetItemListByLoc(Throwable throwable);
        void onSuccessGetItemListByLoc(ArrayList<BJItem> bjItemList);
    }
    public GetItemListByLocPresenter(GetItemListByLocView getItemListByLocView){
        this.getItemListByLocView = getItemListByLocView;
    }

    public void getItemListByLoc(String locationId, String size, String code){
        Observable<ArrayList<BJItem>> observable = new GetItemListByLocInteractor(locationId, size, code).execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<BJItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getItemListByLocView.onErrorGetItemListByLoc(e);
            }

            @Override
            public void onNext(ArrayList<BJItem> bjItemList) {
                getItemListByLocView.onSuccessGetItemListByLoc(bjItemList);
            }
        });
        addSubscription(subscription);
    }
}
