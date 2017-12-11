package com.combintech.baojili.presenter;

import com.combintech.baojili.interactor.AddMemberInteractor;
import com.combintech.baojili.interactor.GetMemberListInteractor;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.User;
import com.combintech.baojili.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.GET;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class GetMemberListPresenter extends BasePresenter {

    private GetMemberListView getMemberListView;
    public interface GetMemberListView{
        void onErrorGetMemberList(Throwable throwable);
        void onSuccessGetMemberList(ArrayList<User> userList);
    }
    public GetMemberListPresenter(GetMemberListView getMemberListView){
        this.getMemberListView = getMemberListView;
    }

    public void getMemberListView(){
        Observable<ArrayList<User>> observable = new GetMemberListInteractor().execute();

        Subscription subscription = observable.subscribe(new Observer<ArrayList<User>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getMemberListView.onErrorGetMemberList(e);
            }

            @Override
            public void onNext(ArrayList<User> userList) {
                getMemberListView.onSuccessGetMemberList(userList);
            }
        });
        addSubscription(subscription);
    }
}
