package com.combintech.baojili.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class BJTrHistory {
    @SerializedName("TrItemHistoryID")
    @Expose
    private String trItemHistoryID;
    @SerializedName("TrItemID")
    @Expose
    private String trItemID;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("Action")
    @Expose
    private String action;
    @SerializedName("PreviousValue")
    @Expose
    private String previousValue;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;

    public String getTrItemHistoryID() {
        return trItemHistoryID;
    }

    public String getTrItemID() {
        return trItemID;
    }

    public String getUserID() {
        return userID;
    }

    public String getAction() {
        return action;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getDateTime() {
        return dateTime;
    }
}
