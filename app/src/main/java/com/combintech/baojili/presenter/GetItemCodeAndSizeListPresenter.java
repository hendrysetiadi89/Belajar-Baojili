package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.GetCodeAndSizeListInteractor;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.CodeAndSizeList;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemCodeAndSizeListPresenter extends BasePresenter {

    private GetItemCodeAndSizeListView getItemCodeAndSizeListView;
    public interface GetItemCodeAndSizeListView{
        void onErrorGetItemCodeAndSizeList(Throwable throwable);
        void onSuccessGetItemCodeAndSizeList(CodeAndSizeList codeAndSizeList);
    }
    public GetItemCodeAndSizeListPresenter(GetItemCodeAndSizeListView getItemCodeAndSizeListView){
        this.getItemCodeAndSizeListView = getItemCodeAndSizeListView;
    }

    public void getItemCodeAndSizeList(){
        Observable<CodeAndSizeList> observable = new GetCodeAndSizeListInteractor().execute();

        Subscription subscription = observable.subscribe(new Observer<CodeAndSizeList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getItemCodeAndSizeListView.onErrorGetItemCodeAndSizeList(e);
            }

            @Override
            public void onNext(CodeAndSizeList codeAndSizeList) {
                getItemCodeAndSizeListView.onSuccessGetItemCodeAndSizeList(codeAndSizeList);
            }
        });
        addSubscription(subscription);
    }
}
