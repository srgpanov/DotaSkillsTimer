package com.srgpanov.dotaskillstimer.data;

import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface HeroDao {
    @Query("SELECT * FROM hero")
    Single<List<Hero>> getAllHeroes();
    @Query("SELECT * FROM ability")
    Single<List<Ability>> getAllAbilities();

    @Query("SELECT * FROM item ORDER BY callDown")
    Single<List<Item>> getAllItems();

    @Query("SELECT * FROM hero WHERE name = :name")
    Single<Hero> getHeroByName(String name);

    @Query("SELECT * FROM item WHERE name = :name")
    Single<Item> getItemByName(String name);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Hero hero);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ability ability);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Update
    void update(Hero hero);

    @Delete
    void delete(Hero hero);

    @Transaction
    @Query("SELECT * from hero ORDER BY name")
    Single<List<HeroWithAbility>> getAllHeroWithAbility();

    @Transaction
    @Query("SELECT * from hero WHERE name =:name")
    Single<HeroWithAbility> getHeroWithAbilityByName(String name);
    @Transaction
    @Query("SELECT *FROM ability WHERE heroName = :heroName")
    Flowable<List<Ability>> getHeroAbility(String heroName);
    @Query("SELECT * FROM ability WHERE name=:name")
    Single<Ability> getAbility(String name);
}
