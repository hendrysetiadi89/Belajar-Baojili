package com.combintech.baojili.interactor.base;

import rx.Observable;

/**
 * Created by Hendry Setiadi
 */

public abstract class BaseInteractor<T> {

    public abstract Observable<T> execute();
}
