package com.flycode.paradoxidealmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by acerkinght on 8/8/16.
 */
public class IdealService extends RealmObject implements Parcelable {
    @SerializedName("_id")
    @PrimaryKey
    private String id;
    private String name;
    @SerializedName("super")
    private String superService;
    private String image;
    private String color;
    private int cost;
    private boolean countable;
    private boolean isFinal;

    public IdealService() {

    }

    protected IdealService(Parcel in) {
        id = in.readString();
        name = in.readString();
        superService = in.readString();
        image = in.readString();
        color = in.readString();
        cost = in.readInt();
        countable = in.readByte() != 0;
        isFinal = in.readByte() != 0;
    }

    public static final Creator<IdealService> CREATOR = new Creator<IdealService>() {
        @Override
        public IdealService createFromParcel(Parcel in) {
            return new IdealService(in);
        }

        @Override
        public IdealService[] newArray(int size) {
            return new IdealService[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuperService() {
        return superService;
    }

    public void setSuperService(String superService) {
        this.superService = superService;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isCountable() {
        return countable;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(superService);
        parcel.writeString(image);
        parcel.writeString(color);
        parcel.writeInt(cost);
        parcel.writeByte((byte) (countable ? 1 : 0));
        parcel.writeByte((byte) (isFinal ? 1 : 0));
    }
}