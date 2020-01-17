package com.example.dotaskillstimer.data;

import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HeroRepositoryImpl implements HeroRepository {
    private final String TAG = this.getClass().getSimpleName();
    private HeroDao mHeroDao;

    public HeroRepositoryImpl(HeroDatabase database) {
        mHeroDao = database.heroDao();
    }

    @Override
    public Maybe<Hero> getHeroByName(String name) {

        return mHeroDao.getByName(name);

    }

    @Override
    public Maybe<Ability> getAbilityByName(String name) {
        return mHeroDao.getAbility(name);
    }

    @Override
    public Maybe<HeroWithAbility> getHeroWithAbilityByName(String name) {
        return mHeroDao.getHeroWithAbilityByName(name);
    }

    @Override
    public Flowable<List<Hero>> getAllHeroes() {
        return mHeroDao.getAllHeroes();
    }



    @Override
    public Flowable<List<HeroWithAbility>> getAllHeroesWithAbilities() {
        return mHeroDao.getAllHeroWithAbility();
    }
    @Override
    public Flowable<List<Ability>> getAllAbilities() {
        return mHeroDao.getAllAbilities();
    }


    @Override
    public void addHero(Hero hero) {
        Disposable disposable=Completable.fromAction(() -> mHeroDao.insert(hero))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, hero.getName()),
                        throwable -> Log.d(TAG, "Hero not added:" + throwable.getMessage())
                );


    }
    @Override
    public void addAbility(Ability ability) {
        Disposable disposable=Completable.fromAction(() -> mHeroDao.insert(ability))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, "Ability added:" + ability.getName()),
                        throwable -> Log.d(TAG, "Ability not added:" + throwable.getMessage())
                );


    }



    @Override
    public void updateHero(Hero hero) {
        mHeroDao.update(hero);
    }

    @Override
    public void deleteHero(Hero hero) {
        mHeroDao.delete(hero);
    }

    public Flowable<List<Ability>> getAbilityOfHero(String heroName) {
        return mHeroDao.getHeroAbility(heroName);
    }

}
