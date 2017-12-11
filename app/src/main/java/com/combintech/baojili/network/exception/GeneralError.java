package com.combintech.baojili.network.exception;

import com.google.gson.annotations.SerializedName;

public class GeneralError {
    @SerializedName("error")
    String message;

    public GeneralError(String message) {
        this.message = message;
    }

    public GeneralError() {
    }

    public String getMessage() {
        return message;
    }
}
