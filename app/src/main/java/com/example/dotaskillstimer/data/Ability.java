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
    private int position;

    @ColumnInfo(index = true)
    private String heroName;

    @Ignore
    private float callDownReduction;

    public Ability(@NonNull String name, int callDown, String icon, int position, String heroName) {
        this.name = name;
        this.callDown = callDown;
        this.icon = icon;
        this.position = position;
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

    public int getCallDownWithReduction() {
        return (int) (callDown*(1-callDownReduction));
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public float getCallDownReduction() {
        return callDownReduction;
    }

    public void setCallDownReduction(float callDownReduction) {
        this.callDownReduction = callDownReduction;
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
        dest.writeInt(this.position);
        dest.writeString(this.heroName);
        dest.writeFloat(this.callDownReduction);
    }

    protected Ability(Parcel in) {
        this.name = in.readString();
        this.callDown = in.readInt();
        this.icon = in.readString();
        this.position = in.readInt();
        this.heroName = in.readString();
        this.callDownReduction = in.readFloat();
    }

    public static final Creator<Ability> CREATOR = new Creator<Ability>() {
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
