package com.combintech.baojili.network;

import android.support.annotation.NonNull;

import com.combintech.baojili.network.errormodel.BaseResponseError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Hendry Setiadi
 */

public class ErrorResponseInterceptor implements Interceptor {
    private static final int BYTE_COUNT = 2048;

    private Class<? extends BaseResponseError> responseErrorClass;
    private BaseResponseError responseError;

    public ErrorResponseInterceptor(@NonNull Class<? extends BaseResponseError> responseErrorClass) {
        this.responseErrorClass = responseErrorClass;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        ResponseBody responseBody = null;
        String responseBodyString = "";
        if (null != response) {
            responseBody = response.peekBody(BYTE_COUNT);
            responseBodyString = responseBody.string();

            Gson gson = new Gson();
            responseError = null;
            try {
                responseError = gson.fromJson(responseBodyString, responseErrorClass);
            } catch (JsonSyntaxException e) { // the json might not be TkpdResponseError instance, so just return it
                return response;
            }
            if (responseError == null) { // no error object
                return response;
            } else {
                if (responseError.hasBody() ) {
                    throw responseError.createException();
                }
                else {
                    return response;
                }
            }
        } else {
            return null;
        }
    }
}
