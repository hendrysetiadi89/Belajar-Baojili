package com.combintech.baojili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class BJLocation implements Parcelable {
    @SerializedName("LocationID")
    @Expose
    private String locationID;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("Longitude")
    @Expose
    private String longitude;

    public String getLocationID() {
        return locationID;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.locationID);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.address);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }

    public BJLocation() {
    }

    protected BJLocation(Parcel in) {
        this.locationID = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.address = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
    }

    public static final Parcelable.Creator<BJLocation> CREATOR = new Parcelable.Creator<BJLocation>() {
        @Override
        public BJLocation createFromParcel(Parcel source) {
            return new BJLocation(source);
        }

        @Override
        public BJLocation[] newArray(int size) {
            return new BJLocation[size];
        }
    };
}
