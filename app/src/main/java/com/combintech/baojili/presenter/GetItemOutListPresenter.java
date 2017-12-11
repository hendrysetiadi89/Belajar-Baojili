package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetItemInListInteractor;
import com.combintech.baojili.interactor.GetItemOutListInteractor;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemOutListPresenter extends BasePresenter {

    private GetItemOutListView getItemOutListView;
    public interface GetItemOutListView{
        void onErrorGetItemOutList(Throwable throwable);
        void onSuccessGetItemOutList(ArrayList<ItemInOut> itemInOutList);
    }
    public GetItemOutListPresenter(GetItemOutListView getItemOutListView){
        this.getItemOutListView = getItemOutListView;
    }

    public void getItemOutList(String locationId, int start, int row, boolean isDeleted){
        Observable<ArrayList<ItemInOut>> observable =
                new GetItemOutListInteractor(locationId, start, row, isDeleted).execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<ItemInOut>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getItemOutListView.onErrorGetItemOutList(e);
            }

            @Override
            public void onNext(ArrayList<ItemInOut> itemInOutList) {
                getItemOutListView.onSuccessGetItemOutList(itemInOutList);
            }
        });
        addSubscription(subscription);
    }
}
