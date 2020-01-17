package com.example.dotaskillstimer.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Hero implements Parcelable {
    @NonNull
    @PrimaryKey
    private String name;
    private String avatar;
    @TypeConverters(PrimaryAttributesConverter.class)
    private PrimaryAttributes primaryAttributes;

    public Hero(@NonNull String name, String avatar, PrimaryAttributes primaryAttributes) {
        this.name = name;
        this.avatar = avatar;
        this.primaryAttributes = primaryAttributes;
    }
    @Ignore
    public Hero() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public PrimaryAttributes getPrimaryAttributes() {
        return primaryAttributes;
    }

    public void setPrimaryAttributes(PrimaryAttributes primaryAttributes) {
        this.primaryAttributes = primaryAttributes;
    }

    @NonNull
    @Override
    public String toString() {
        String hero = "Hero: "+name +" "+"Primary attr: "+ primaryAttributes.toString() +" "+avatar+"avatar: ";
        return hero;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeInt(this.primaryAttributes == null ? -1 : this.primaryAttributes.ordinal());
    }
    protected Hero(Parcel in) {
        this.name = in.readString();
        this.avatar = in.readString();
        int tmpPrimaryAttributes = in.readInt();
        this.primaryAttributes = tmpPrimaryAttributes == -1 ? null : PrimaryAttributes.values()[tmpPrimaryAttributes];
    }

    public static final Parcelable.Creator<Hero> CREATOR = new Parcelable.Creator<Hero>() {
        @Override
        public Hero createFromParcel(Parcel source) {
            return new Hero(source);
        }

        @Override
        public Hero[] newArray(int size) {
            return new Hero[size];
        }
    };
}
