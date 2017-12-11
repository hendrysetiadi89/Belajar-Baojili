package com.combintech.baojili.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class BJStock {
    @SerializedName("StockID")
    @Expose
    private String stockID;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("LocationID")
    @Expose
    private String locationID;
    @SerializedName("Stock")
    @Expose
    private String stock;

    public String getStockID() {
        return stockID;
    }

    public String getItemID() {
        return itemID;
    }

    public String getLocationID() {
        return locationID;
    }

    public String getStock() {
        return stock;
    }
}
