package com.combintech.baojili.presenter;

import android.text.TextUtils;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.apphelper.PreferenceMsItemHelper;
import com.combintech.baojili.interactor.GetItemListInteractor;
import com.combintech.baojili.interactor.GetLocationListInteractor;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetItemListPresenter extends BasePresenter {

    private GetItemListView getItemListView;
    public interface GetItemListView{
        void onErrorGetItemList(Throwable throwable);
        void onSuccessGetItemList(ArrayList<BJItem> bjItemList);
    }
    public GetItemListPresenter(GetItemListView getItemListView){
        this.getItemListView = getItemListView;
    }

    public void getItemList(final String itemId, final String size, final String code){
        if (PreferenceMsItemHelper.isExpired()) {
            Observable<ArrayList<BJItem>> observable = new GetItemListInteractor("", "","").execute();

            Subscription subscription = observable.subscribe(new Observer<ArrayList<BJItem>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    getItemListView.onErrorGetItemList(e);
                }

                @Override
                public void onNext(ArrayList<BJItem> bjItemArrayList) {
                    PreferenceMsItemHelper.setMsItem(bjItemArrayList);
                    searchFromLocal(itemId, size, code, bjItemArrayList);
                }
            });
            addSubscription(subscription);
        } else {
            searchFromLocal(itemId, size, code, PreferenceMsItemHelper.getMsItem());
        }
    }

    private void searchFromLocal(final String itemId, final String size, final String code, ArrayList<BJItem> bjItemArrayList){
        if (TextUtils.isEmpty(itemId) && TextUtils.isEmpty(size) && TextUtils.isEmpty(code)) {
            if (bjItemArrayList == null || bjItemArrayList.size() == 0) {
                getItemListView.onErrorGetItemList(RetrofitException.dataError("Produk tidak ditemukan"));
            } else {
                getItemListView.onSuccessGetItemList(bjItemArrayList);
            }
        } else {
            boolean isItemIdEmpty = TextUtils.isEmpty(itemId);
            boolean isCodeEmpty = TextUtils.isEmpty(code);
            boolean isSizeEmpty = TextUtils.isEmpty(size);

            ArrayList<BJItem> searchResultBJItemArrayList = new ArrayList<>();
            for (int i = 0, sizei = bjItemArrayList.size(); i<sizei; i++) {
                BJItem bjItem = bjItemArrayList.get(i);
                if (!isItemIdEmpty) {
                    if (! itemId.equals(bjItem.getItemID())) {
                        continue;
                    }
                }
                if (!isCodeEmpty) {
                    if (! code.equals(bjItem.getCode())) {
                        continue;
                    }
                }
                if (!isSizeEmpty) {
                    if (! size.equals(bjItem.getSize())) {
                        continue;
                    }
                }
                searchResultBJItemArrayList.add(bjItem);
                if (!isItemIdEmpty) { // only one data needed. exit from loop
                    break;
                }
                if (!isCodeEmpty && !isSizeEmpty) { // only one data needed. exit from loop
                    break;
                }
            }
            if (searchResultBJItemArrayList.size() == 0) {
                getItemListView.onErrorGetItemList(RetrofitException.dataError("Produk tidak ditemukan"));
            } else {
                getItemListView.onSuccessGetItemList(searchResultBJItemArrayList);
            }
        }
    }
}
