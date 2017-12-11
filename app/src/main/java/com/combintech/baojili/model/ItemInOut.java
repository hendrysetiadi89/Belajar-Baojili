package com.combintech.baojili.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hendry Setiadi
 */

public class ItemInOut implements Parcelable {
    @SerializedName("TrItemID")
    @Expose
    private String trItemID;
    @SerializedName("ItemID")
    @Expose
    private String itemID;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("ItemType")
    @Expose
    private String itemType;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Size")
    @Expose
    private String size;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("SourceLocationID")
    @Expose
    private String sourceLocationID;
    @SerializedName("SourceLocationName")
    @Expose
    private String sourceLocationName;
    @SerializedName("TargetLocationID")
    @Expose
    private String targetLocationID;
    @SerializedName("TargetLocationName")
    @Expose
    private String targetLocationName;
    @SerializedName("TrType")
    @Expose
    private String trType;
    @SerializedName("Quantity")
    @Expose
    private String quantity;
    @SerializedName("Datetime")
    @Expose
    private String datetime;
    @SerializedName("Deleted")
    @Expose
    private String deleted;
    @SerializedName("DeletedBy")
    @Expose
    private String deletedBy;
    @SerializedName("DeletedAt")
    @Expose
    private String deletedAt;

    @SerializedName("UpdatedBy")
    @Expose
    private String updatedBy;
    @SerializedName("UpdatedAt")
    @Expose
    private String updatedAt;

    public String getTrItemID() {
        return trItemID;
    }

    public String getItemID() {
        return itemID;
    }

    public String getDescription() {
        return description;
    }

    public String getItemType() {
        return itemType;
    }

    public String getCode() {
        return code;
    }

    public String getSize() {
        return size;
    }

    public String getUserID() {
        return userID;
    }

    public String getSourceLocationID() {
        return sourceLocationID;
    }

    public String getSourceLocationName() {
        return sourceLocationName;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public String getTargetLocationName() {
        return targetLocationName;
    }

    public String getTrType() {
        return trType;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getDeleted() {
        return deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trItemID);
        dest.writeString(this.itemID);
        dest.writeString(this.description);
        dest.writeString(this.itemType);
        dest.writeString(this.code);
        dest.writeString(this.size);
        dest.writeString(this.userID);
        dest.writeString(this.sourceLocationID);
        dest.writeString(this.sourceLocationName);
        dest.writeString(this.targetLocationID);
        dest.writeString(this.targetLocationName);
        dest.writeString(this.trType);
        dest.writeString(this.quantity);
        dest.writeString(this.datetime);
        dest.writeString(this.deleted);
        dest.writeString(this.deletedBy);
        dest.writeString(this.deletedAt);
        dest.writeString(this.updatedBy);
        dest.writeString(this.updatedAt);
    }

    public ItemInOut() {
    }

    protected ItemInOut(Parcel in) {
        this.trItemID = in.readString();
        this.itemID = in.readString();
        this.description = in.readString();
        this.itemType = in.readString();
        this.code = in.readString();
        this.size = in.readString();
        this.userID = in.readString();
        this.sourceLocationID = in.readString();
        this.sourceLocationName = in.readString();
        this.targetLocationID = in.readString();
        this.targetLocationName = in.readString();
        this.trType = in.readString();
        this.quantity = in.readString();
        this.datetime = in.readString();
        this.deleted = in.readString();
        this.deletedBy = in.readString();
        this.deletedAt = in.readString();
        this.updatedBy = in.readString();
        this.updatedAt = in.readString();
    }

    public static final Parcelable.Creator<ItemInOut> CREATOR = new Parcelable.Creator<ItemInOut>() {
        @Override
        public ItemInOut createFromParcel(Parcel source) {
            return new ItemInOut(source);
        }

        @Override
        public ItemInOut[] newArray(int size) {
            return new ItemInOut[size];
        }
    };
}
