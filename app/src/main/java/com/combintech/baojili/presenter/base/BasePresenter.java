package com.combintech.baojili.presenter.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hendry Setiadi
 */

public class BasePresenter {
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public final void onResume() {
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed()) {
            compositeSubscription = new CompositeSubscription();
        }
    }

    public final void onPause() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    protected void addSubscription(Subscription subscription){
        onResume();
        compositeSubscription.add(subscription);
    }
}
