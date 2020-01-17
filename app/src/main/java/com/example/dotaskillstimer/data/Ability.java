package com.example.dotaskillstimer.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Hero.class,parentColumns = "name",childColumns = "heroName"))
public class Ability implements Parcelable {
    @NonNull
    @PrimaryKey
    private String name;
    private int callDown;
    private String icon;
    @ColumnInfo(index = true)
    private String heroName;


    public Ability(@NonNull String name, int callDown, String icon, String heroName) {
        this.name = name;
        this.callDown = callDown;
        this.icon = icon;
        this.heroName = heroName;
    }
    @Ignore
    public Ability(@NonNull String name, int callDown, String heroName) {
        this.name = name;
        this.callDown = callDown;
        this.heroName = heroName;
    }
    @Ignore
    public Ability() {
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
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

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ability name="+name+" callDown = "+callDown+" icon="+icon+"heroName="+heroName;
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
        dest.writeString(this.heroName);
    }

    protected Ability(Parcel in) {
        this.name = in.readString();
        this.callDown = in.readInt();
        this.icon = in.readString();
        this.heroName = in.readString();
    }
    public static final Parcelable.Creator<Ability> CREATOR = new Parcelable.Creator<Ability>() {
        @Override
        public Ability createFromParcel(Parcel source) {
            return new Ability(source);
        }

        @Override
        public Ability[] newArray(int size) {
            return new Ability[size];
        }
    };
}
