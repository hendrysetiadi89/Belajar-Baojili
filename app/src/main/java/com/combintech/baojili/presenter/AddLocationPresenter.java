package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.AddLocationInteractor;
import com.combintech.baojili.interactor.AddMemberInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class AddLocationPresenter extends BasePresenter {

    private AddLocationView addLocationView;
    public interface AddLocationView{
        void onErrorAddLocation(Throwable throwable);
        void onSuccessAddLocation(MessageResponse messageResponse);
    }
    public AddLocationPresenter(AddLocationView addLocationView){
        this.addLocationView = addLocationView;
    }

    public void addLocation(String name, String type, String address, String latitude, String longitude){
        Observable<MessageResponse> observable = new AddLocationInteractor(name,type, address, latitude, longitude).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                addLocationView.onErrorAddLocation(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceLocationHelper.setExpired();
                addLocationView.onSuccessAddLocation(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
