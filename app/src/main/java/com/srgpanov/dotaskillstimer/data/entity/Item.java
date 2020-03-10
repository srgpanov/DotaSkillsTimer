package com.srgpanov.dotaskillstimer.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Item implements Parcelable {
    @NonNull
    @PrimaryKey
    private String name;
    private int callDown;
    private String icon;
    @Ignore
    private float callDownReduction;

    public Item(String name, int callDown, String icon) {
        this.name = name;
        this.callDown = callDown;
        this.icon = icon;
    }
    @Ignore
    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCallDown() {
        return callDown;
    }

    public void setCallDown(int callDown) {
        this.callDown = callDown;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getCallDownReduction() {
        return callDownReduction;
    }

    public void setCallDownReduction(float callDownReduction) {
        this.callDownReduction = callDownReduction;
    }
    public int getCallDownWithReduction() {
        return (int) (callDown*(1-callDownReduction));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.callDown);
        dest.writeString(this.icon);
        dest.writeFloat(this.callDownReduction);
    }

    protected Item(Parcel in) {
        this.name = in.readString();
        this.callDown = in.readInt();
        this.icon = in.readString();
        this.callDownReduction = in.readFloat();
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

    @NonNull
    @Override
    public String toString() {
        return "item "+name;
    }
}
