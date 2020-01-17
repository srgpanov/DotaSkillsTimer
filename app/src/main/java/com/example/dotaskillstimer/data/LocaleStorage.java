package com.example.dotaskillstimer.data;

import android.content.Context;

import androidx.room.Room;

public class LocaleStorage {
    private HeroDatabase heroDatabase;

    public LocaleStorage(Context  context) {
        heroDatabase = Room.databaseBuilder(context,HeroDatabase.class,"HeroDatabase").build();
    }


}
