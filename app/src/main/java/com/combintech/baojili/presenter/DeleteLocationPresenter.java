package com.combintech.baojili.presenter;

import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.interactor.DeleteLocationInteractor;
import com.combintech.baojili.interactor.UpdatePasswordInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.base.BasePresenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class DeleteLocationPresenter extends BasePresenter {

    private DeleteLocationView deleteLocationView;
    public interface DeleteLocationView{
        void onErrorDeleteLocation(Throwable throwable);
        void onSuccessDeleteLocation(MessageResponse messageResponse);
    }
    public DeleteLocationPresenter(DeleteLocationView deleteLocationView){
        this.deleteLocationView = deleteLocationView;
    }

    public void deleteLocation(String locationId){
        Observable<MessageResponse> observable = new DeleteLocationInteractor(locationId).execute();

        Subscription subscription = observable.subscribe(new Observer<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                deleteLocationView.onErrorDeleteLocation(e);
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                PreferenceLocationHelper.setExpired();
                deleteLocationView.onSuccessDeleteLocation(messageResponse);
            }
        });
        addSubscription(subscription);
    }
}
