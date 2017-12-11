package com.combintech.baojili.network.errormodel;

import android.text.TextUtils;

import com.combintech.baojili.network.exception.RetrofitException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class ErrorResponse extends BaseResponseError {

    private static final String ERROR_KEY = "error";

    @SerializedName(ERROR_KEY)
    @Expose
    private String errorMessage;

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

    @Override
    public boolean hasBody() {
        return !TextUtils.isEmpty(errorMessage);
    }

    @Override
    public RetrofitException createException() {
        return RetrofitException.dataError(errorMessage);
    }
}
