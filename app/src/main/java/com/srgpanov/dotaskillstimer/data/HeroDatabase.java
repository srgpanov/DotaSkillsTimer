package com.srgpanov.dotaskillstimer.data;

import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Hero.class, Ability.class, Item.class},version = 1,exportSchema = false)
public abstract class HeroDatabase extends RoomDatabase {
    public abstract HeroDao heroDao();
}
