package com.example.dotaskillstimer.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface HeroDao {
    @Query("SELECT * FROM hero")
    Flowable<List<Hero>> getAllHeroes();
    @Query("SELECT * FROM ability")
    Flowable<List<Ability>> getAllAbilities();

    @Query("SELECT * FROM hero WHERE name = :name")
    Maybe<Hero> getByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Hero hero);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ability ability);

    @Update
    void update(Hero hero);

    @Delete
    void delete(Hero hero);

    @Transaction
    @Query("SELECT * from hero")
    Flowable<List<HeroWithAbility>> getAllHeroWithAbility();

    @Transaction
    @Query("SELECT * from hero WHERE name =:name")
    Maybe<HeroWithAbility> getHeroWithAbilityByName(String name);
    @Transaction
    @Query("SELECT *FROM ability WHERE heroName = :heroName")
    Flowable<List<Ability>> getHeroAbility(String heroName);
    @Query("SELECT * FROM ability WHERE name=:name")
    Maybe<Ability> getAbility(String name);
}
