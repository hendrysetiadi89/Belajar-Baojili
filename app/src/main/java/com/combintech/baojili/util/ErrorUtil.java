package com.combintech.baojili.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import com.combintech.baojili.network.exception.GeneralError;
import com.combintech.baojili.network.exception.RetrofitException;

import java.io.IOException;


public class ErrorUtil {

    private static final int TOAST_LONG_SIZE = 20; //20 characters

    public static void showToast(Context context, Throwable e){
        String message = getMessageFromException(e);
        if (!TextUtils.isEmpty(message)) {
            showToast(context, message);
        }
    }

    public static String getMessageFromException(Throwable e){
        if (e instanceof RetrofitException) {
            RetrofitException error = (RetrofitException) e;
            if (error.getKind() == RetrofitException.Kind.NETWORK ||
                    error.getKind() == RetrofitException.Kind.HTTP) {
                return "Terjadi kesalahan koneksi. Silakan coba kembali";
            }
            try {
                GeneralError err = error.getErrorBodyAs(GeneralError.class);
                if (err != null && !TextUtils.isEmpty(err.getMessage())) {
                    return err.getMessage();
                } else if (err == null &&
                        !TextUtils.isEmpty(error.getMessage())) {
                    return error.getMessage();
                } else {
                    return null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } else {
            return "Terjadi kesalahan koneksi. Silakan coba kembali";
        }
    }

    public static void showToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, message.length()> TOAST_LONG_SIZE ? Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
