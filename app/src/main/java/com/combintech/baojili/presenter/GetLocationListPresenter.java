package com.combintech.baojili.presenter;

import android.text.TextUtils;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.GetLocationListInteractor;
import com.combintech.baojili.interactor.GetMemberListInteractor;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.User;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetLocationListPresenter extends BasePresenter {

    private GetLocationListView getLocationListView;
    public interface GetLocationListView{
        void onErrorGetLocationList(Throwable throwable);
        void onSuccessGetLocationList(ArrayList<BJLocation> bjLocationList);
    }
    public GetLocationListPresenter(GetLocationListView getLocationListView){
        this.getLocationListView = getLocationListView;
    }

    public void getLocationList(String locationName){
        if (PreferenceLocationHelper.isExpired()) {
            Observable<ArrayList<BJLocation>> observable = new GetLocationListInteractor("").execute();

            Subscription subscription = observable.subscribe(new Observer<ArrayList<BJLocation>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    getLocationListView.onErrorGetLocationList(e);
                }

                @Override
                public void onNext(ArrayList<BJLocation> bjLocationList) {
                    PreferenceLocationHelper.setMsLocation(bjLocationList);
                    getLocationListView.onSuccessGetLocationList(bjLocationList);
                }
            });
            addSubscription(subscription);
        } else {
            getLocationListView.onSuccessGetLocationList(PreferenceLocationHelper.getMsLocation());
        }

    }
}
