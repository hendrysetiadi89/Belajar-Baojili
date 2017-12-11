package com.combintech.baojili.network.errormodel;

import com.combintech.baojili.network.exception.RetrofitException;

/**
 * Created by Hendry Setiadi
 */

public abstract class BaseResponseError {

    /**
     * @return the String key if the json Response indicated that is an error response.
     */
    public abstract String getErrorKey();

    /**
     * @return if the error is filled, return true
     */
    public abstract boolean hasBody();

    /**
     * @return the exception from this Error
     */
    public abstract RetrofitException createException();

}
