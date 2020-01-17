package com.example.dotaskillstimer.data;

import com.google.firebase.database.IgnoreExtraProperties;

import androidx.room.TypeConverters;

@IgnoreExtraProperties
public class HeroFireBase {
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

    public String getPrimaryAttributes() {
        return primaryAttributes;
    }

    public void setPrimaryAttributes(String primaryAttributes) {
        this.primaryAttributes = primaryAttributes;
    }

    private String name;
    private String avatar;
    private String primaryAttributes;

    public HeroFireBase(String name, String avatar, String primaryAttributes) {
        this.name = name;
        this.avatar = avatar;
        this.primaryAttributes = primaryAttributes;
    }

    public HeroFireBase() {
    }
}
