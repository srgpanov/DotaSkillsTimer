package com.example.dotaskillstimer.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Relation;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class HeroWithAbility  implements Parcelable {
    private String name;
    private String avatar;
    @TypeConverters(PrimaryAttributesConverter.class)
    private PrimaryAttributes primaryAttributes;
    @Relation(parentColumn = "name",entityColumn = "heroName")
    private List<Ability> abilities;

    public String getName() {
        return name;
    }

    public PrimaryAttributes getPrimaryAttributes() {
        return primaryAttributes;
    }

    public void setPrimaryAttributes(PrimaryAttributes primaryAttributes) {
        this.primaryAttributes = primaryAttributes;
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

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    @NonNull
    @Override
    public String toString() {
        if(abilities!=null) {
            return "Hero name: " + name + "  has " + abilities.size() + " abilities" + " primaryAttributes is " + primaryAttributes;
        }else return "Hero name: " + name + "  has " + null + " abilities" + " primaryAttributes is " + primaryAttributes;
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
        dest.writeList(this.abilities);
    }

    public HeroWithAbility() {
    }

    protected HeroWithAbility(Parcel in) {
        this.name = in.readString();
        this.avatar = in.readString();
        int tmpPrimaryAttributes = in.readInt();
        this.primaryAttributes = tmpPrimaryAttributes == -1 ? null : PrimaryAttributes.values()[tmpPrimaryAttributes];
        this.abilities = new ArrayList<Ability>();
        in.readList(this.abilities, Ability.class.getClassLoader());
    }

    public static final Parcelable.Creator<HeroWithAbility> CREATOR = new Parcelable.Creator<HeroWithAbility>() {
        @Override
        public HeroWithAbility createFromParcel(Parcel source) {
            return new HeroWithAbility(source);
        }

        @Override
        public HeroWithAbility[] newArray(int size) {
            return new HeroWithAbility[size];
        }
    };

//    @Override
//    public boolean equals(@Nullable Object obj) {
//        boolean name =this.name.equals(((HeroWithAbility)obj).name);
//        Log.d("HeroWithAbility equals", String.valueOf(name));
//        return name;
//    }
}
