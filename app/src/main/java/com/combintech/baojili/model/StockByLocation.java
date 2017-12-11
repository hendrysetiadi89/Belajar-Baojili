package com.combintech.baojili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class StockByLocation implements Parcelable {
    public static class Item implements Parcelable {
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("size")
        @Expose
        private String size;
        @SerializedName("stock")
        @Expose
        private String stock;

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public String getPhoto() {
            return photo;
        }

        public String getType() {
            return type;
        }

        public String getSize() {
            return size;
        }

        public String getStock() {
            return stock;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.code);
            dest.writeString(this.desc);
            dest.writeString(this.photo);
            dest.writeString(this.type);
            dest.writeString(this.size);
            dest.writeString(this.stock);
        }

        public Item() {
        }

        protected Item(Parcel in) {
            this.code = in.readString();
            this.desc = in.readString();
            this.photo = in.readString();
            this.type = in.readString();
            this.size = in.readString();
            this.stock = in.readString();
        }

        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("total_stock")
    @Expose
    private String totalStock;
    @SerializedName("item")
    @Expose
    private List<Item> item = null;

    public String getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getTotalStock() {
        return totalStock;
    }

    public List<Item> getItem() {
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.locationId);
        dest.writeString(this.locationName);
        dest.writeString(this.totalStock);
        dest.writeTypedList(this.item);
    }

    public StockByLocation() {
    }

    protected StockByLocation(Parcel in) {
        this.locationId = in.readString();
        this.locationName = in.readString();
        this.totalStock = in.readString();
        this.item = in.createTypedArrayList(Item.CREATOR);
    }

    public static final Parcelable.Creator<StockByLocation> CREATOR = new Parcelable.Creator<StockByLocation>() {
        @Override
        public StockByLocation createFromParcel(Parcel source) {
            return new StockByLocation(source);
        }

        @Override
        public StockByLocation[] newArray(int size) {
            return new StockByLocation[size];
        }
    };
}
