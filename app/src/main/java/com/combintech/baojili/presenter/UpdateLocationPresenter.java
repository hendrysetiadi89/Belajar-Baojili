package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.AddLocationInteractor;
import com.combintech.baojili.interactor.UpdateLocationInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class UpdateLocationPresenter extends BasePresenter {

    private UpdateLocationView updateLocationView;
    public interface UpdateLocationView{
        void onErrorUpdateLocation(Throwable throwable);
        void onSuccessUpdateLocation(MessageResponse messageResponse);
    }
    public UpdateLocationPresenter(UpdateLocationView updateLocationView){
        this.updateLocationView = updateLocationView;
    }

    public void updateLocation(String locationId, String name, String type, String address, String latitude, String longitude){
        Observable<MessageResponse> observable = new UpdateLocationInteractor(
                locationId, name,type, address, latitude, longitude).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                updateLocationView.onErrorUpdateLocation(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceLocationHelper.setExpired();
                updateLocationView.onSuccessUpdateLocation(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
