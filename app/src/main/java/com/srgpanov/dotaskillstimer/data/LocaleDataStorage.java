package com.srgpanov.dotaskillstimer.data;

import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class LocaleDataStorage {
    private final String TAG = this.getClass().getSimpleName();
    private HeroDao mHeroDao;

    public LocaleDataStorage(HeroDatabase database) {
        mHeroDao = database.heroDao();
    }

    public Single<Hero> getHeroByName(String name) {

        return mHeroDao.getHeroByName(name);

    }

    public Single<Ability> getAbilityByName(String name) {
        return mHeroDao.getAbility(name);
    }

    public Single<HeroWithAbility> getHeroWithAbilityByName(String name) {
        return mHeroDao.getHeroWithAbilityByName(name);
    }

    public Single<List<Hero>> getAllHeroes() {
        return mHeroDao.getAllHeroes();
    }



    public Single<List<HeroWithAbility>> getAllHeroesWithAbilities() {
        return mHeroDao.getAllHeroWithAbility();
    }
    public Single<List<Ability>> getAllAbilities() {
        return mHeroDao.getAllAbilities();
    }


    public Single<List<Item>> getAllItems() {
        return mHeroDao.getAllItems();
    }
    public Completable addHero(Hero hero) {
        return Completable.fromAction(() -> mHeroDao.insert(hero))
                .subscribeOn(Schedulers.io());


    }

    public Completable addAbility(Ability ability) {
        return Completable.fromAction(() -> mHeroDao.insert(ability))
                .subscribeOn(Schedulers.io());

    }

    public Completable addItem(Item item) {
        return Completable.fromAction(() -> mHeroDao.insert(item))
                .subscribeOn(Schedulers.io());
    }


    public void updateHero(Hero hero) {
        mHeroDao.update(hero);
    }

    public void deleteHero(Hero hero) {
        mHeroDao.delete(hero);
    }

    public Flowable<List<Ability>> getAbilityOfHero(String heroName) {
        return mHeroDao.getHeroAbility(heroName);
    }

}
