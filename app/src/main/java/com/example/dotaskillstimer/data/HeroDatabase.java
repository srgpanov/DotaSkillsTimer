package com.example.dotaskillstimer.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Hero.class,Ability.class},version = 1,exportSchema = false)
public abstract class HeroDatabase extends RoomDatabase {
    public abstract HeroDao heroDao();
}
