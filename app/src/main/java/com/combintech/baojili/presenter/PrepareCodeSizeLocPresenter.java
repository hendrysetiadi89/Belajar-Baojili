package com.combintech.baojili.presenter;

import android.preference.Preference;

import com.combintech.baojili.apphelper.PreferenceCodeAndSizeHelper;
import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.GetCodeAndSizeListInteractor;
import com.combintech.baojili.interactor.GetLocationListInteractor;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.CodeAndSizeList;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class PrepareCodeSizeLocPresenter extends BasePresenter {

    private PrepareCodeSizeLocView prepareCodeSizeLocView;
    private String callerIdentifier;

    public interface PrepareCodeSizeLocView{
        void onErrorPrepareCodeSizeLocView(Throwable throwable);
        void onSuccessPrepareCodeSizeLocView(String callerIdentifier);
    }
    public PrepareCodeSizeLocPresenter(PrepareCodeSizeLocView prepareCodeSizeLocView){
        this.prepareCodeSizeLocView = prepareCodeSizeLocView;
    }

    public void getItemCodeSizeLocList(String callerIdentifier){
        this.callerIdentifier = callerIdentifier;
        if (PreferenceLocationHelper.isExpired()) {
            Observable<ArrayList<BJLocation>> observable = new GetLocationListInteractor("").execute();

            Subscription subscription = observable.subscribe(new Observer<ArrayList<BJLocation>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    prepareCodeSizeLocView.onErrorPrepareCodeSizeLocView(e);
                }

                @Override
                public void onNext(ArrayList<BJLocation> bjLocationList) {
                    PreferenceLocationHelper.setMsLocation(bjLocationList);
                    retrieveCodeAndSize();
                }
            });
            addSubscription(subscription);
        } else {
            retrieveCodeAndSize();
        }
    }

    private void retrieveCodeAndSize(){
        if (PreferenceCodeAndSizeHelper.isExpired()) {
            Observable<CodeAndSizeList> observable = new GetCodeAndSizeListInteractor().execute();

            Subscription subscription = observable.subscribe(new Observer<CodeAndSizeList>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    prepareCodeSizeLocView.onErrorPrepareCodeSizeLocView(e);
                }

                @Override
                public void onNext(CodeAndSizeList codeAndSizeList) {
                    PreferenceCodeAndSizeHelper.setMsCodeAndSize(codeAndSizeList.getCode(), codeAndSizeList.getSize());
                    prepareCodeSizeLocView.onSuccessPrepareCodeSizeLocView(callerIdentifier);
                }
            });
            addSubscription(subscription);
        } else {
            prepareCodeSizeLocView.onSuccessPrepareCodeSizeLocView(callerIdentifier);
        }
    }
}
