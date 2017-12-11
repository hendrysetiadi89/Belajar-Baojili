package com.combintech.baojili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class StockByItem implements Parcelable {
    public static class Location implements Parcelable {
        @SerializedName("location_id")
        @Expose
        private String locationId;
        @SerializedName("location_name")
        @Expose
        private String locationName;
        @SerializedName("location_stock")
        @Expose
        private String locationStock;

        public String getLocationId() {
            return locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getLocationStock() {
            return locationStock;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.locationId);
            dest.writeString(this.locationName);
            dest.writeString(this.locationStock);
        }

        public Location() {
        }

        protected Location(Parcel in) {
            this.locationId = in.readString();
            this.locationName = in.readString();
            this.locationStock = in.readString();
        }

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel source) {
                return new Location(source);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("total_stock")
    @Expose
    private String totalStock;
    @SerializedName("location")
    @Expose
    private List<Location> location = null;

    public String getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCode() {
        return code;
    }

    public String getSize() {
        return size;
    }

    public String getTotalStock() {
        return totalStock;
    }

    public List<Location> getLocation() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeString(this.description);
        dest.writeString(this.type);
        dest.writeString(this.photo);
        dest.writeString(this.code);
        dest.writeString(this.size);
        dest.writeString(this.totalStock);
        dest.writeTypedList(this.location);
    }

    public StockByItem() {
    }

    protected StockByItem(Parcel in) {
        this.itemId = in.readString();
        this.description = in.readString();
        this.type = in.readString();
        this.photo = in.readString();
        this.code = in.readString();
        this.size = in.readString();
        this.totalStock = in.readString();
        this.location = in.createTypedArrayList(Location.CREATOR);
    }

    public static final Parcelable.Creator<StockByItem> CREATOR = new Parcelable.Creator<StockByItem>() {
        @Override
        public StockByItem createFromParcel(Parcel source) {
            return new StockByItem(source);
        }

        @Override
        public StockByItem[] newArray(int size) {
            return new StockByItem[size];
        }
    };
}
