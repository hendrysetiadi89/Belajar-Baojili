package com.combintech.baojili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class BJItem implements Parcelable {
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Photo")
    @Expose
    private String photo;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Size")
    @Expose
    private String size;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("Cost")
    @Expose
    private String cost;

    public String getItemID() {
        return itemID;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }

    public String getCost() {
        return cost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemID);
        dest.writeString(this.description);
        dest.writeString(this.photo);
        dest.writeString(this.type);
        dest.writeString(this.code);
        dest.writeString(this.size);
        dest.writeString(this.price);
        dest.writeString(this.cost);
    }

    public BJItem() {
    }

    protected BJItem(Parcel in) {
        this.itemID = in.readString();
        this.description = in.readString();
        this.photo = in.readString();
        this.type = in.readString();
        this.code = in.readString();
        this.size = in.readString();
        this.price = in.readString();
        this.cost = in.readString();
    }

    public static final Parcelable.Creator<BJItem> CREATOR = new Parcelable.Creator<BJItem>() {
        @Override
        public BJItem createFromParcel(Parcel source) {
            return new BJItem(source);
        }

        @Override
        public BJItem[] newArray(int size) {
            return new BJItem[size];
        }
    };
}
