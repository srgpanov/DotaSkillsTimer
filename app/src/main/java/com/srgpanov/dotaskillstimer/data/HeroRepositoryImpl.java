package com.srgpanov.dotaskillstimer.data;

import android.util.Log;

import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HeroRepositoryImpl implements HeroRepository {
    private final String TAG = this.getClass().getSimpleName();
    @Inject
    LocaleDataStorage localeDataStorage;
    @Inject
    CloudFireStore cloudFireStore;
    private int heroesInDotaNow = 119;

    public HeroRepositoryImpl() {
        App.getInstance().getComponentsHolder().getAppComponent().injectDataStorage(this);
    }

    @Override
    public Single<List<Hero>> getAllHeroes() {
        return localeDataStorage
                .getAllHeroes()
                .filter(hero -> hero.size()>=heroesInDotaNow)
                .switchIfEmpty(Single.fromCallable(() -> cloudFireStore.getHeroesList()))
                .doAfterSuccess(heroList -> {
                    Disposable writeToDbDis = Flowable.fromIterable(heroList)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(hero -> localeDataStorage.addHero(hero),
                                    throwable ->logged("Hero was not written") );
                })
                ;

    }

    @Override
    public Single<List<Ability>> getAllAbilities() {
        return localeDataStorage
                .getAllAbilities()
                .filter(abilities -> !abilities.isEmpty())
                .switchIfEmpty(Single.fromCallable(() -> cloudFireStore.getAbilityList()))
                .doAfterSuccess(abilities -> {
                    Disposable writeToDbDis = Flowable.fromIterable(abilities)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(ability -> localeDataStorage.addAbility(ability),
                                    throwable -> {
                                        logged("Ability was not written");
                                    });
                });
    }


    @Override
    public Single<List<HeroWithAbility>> getAllHeroesWithAbilities() {
        Completable dataIsReady = refreshDB();
        return localeDataStorage
                .getAllHeroesWithAbilities()
                .filter(hero -> hero.size()>=heroesInDotaNow)
                .switchIfEmpty(dataIsReady.andThen(localeDataStorage.getAllHeroesWithAbilities()))
                ;
    }

    public Completable refreshDB() {
        Completable loadAndWriteHeroToDb =
                Single.fromCallable(() -> cloudFireStore.getHeroesList())
                        .flatMapObservable((Function<List<Hero>, ObservableSource<Hero>>) Observable::fromIterable)
                        .doOnNext(hero -> logged("hero " + hero.getName() + " loaded"))
                        .flatMapCompletable(hero -> localeDataStorage.addHero(hero))
                        .doOnComplete(() -> logged("heroes added to DB"));

        Completable loadAndWriteAbilityToDb =
                Single.fromCallable(() -> cloudFireStore.getAbilityList())
                        .flatMapObservable((Function<List<Ability>, ObservableSource<Ability>>) Observable::fromIterable)
                        .doOnNext(ability -> logged("ability " + ability.getName() + " loaded"))
                        .flatMapCompletable(ability -> localeDataStorage.addAbility(ability))
                        .doOnComplete(() -> logged("ability added to DB"));
        return loadAndWriteHeroToDb.andThen(loadAndWriteAbilityToDb);
    }

    @Override
    public Single<List<Item>> getAllItems() {
        return localeDataStorage.getAllItems()
                .flatMap(items -> {
                    if (items.isEmpty()) {
                        logged("items loaded from CS");
                        logged("items from CS"+cloudFireStore.getItemsList());
                        saveItemsToDB(cloudFireStore.getItemsList());
                        return Single.just(cloudFireStore.getItemsList());
                    } else {
                        logged("items loaded from DB");
                        logged("items from DB "+items);
                        return Single.just(items);
                    }
                });
    }

    private void saveItemsToDB(List<Item> itemList) {
        logged(itemList+"add items to DB");
        for (Item item : itemList) {
            localeDataStorage.addItem(item).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {
                    logged("Item " + item.getName() + " added to DB");
                }

                @Override
                public void onError(Throwable e) {
                    logged("Item " + item.getName() + " not added to DB");
                }
            });

        }
    }

    @Override
    public Single<Hero> getHeroByName(String name) {
        return localeDataStorage.getHeroByName(name);
    }

    @Override
    public Single<Ability> getAbilityByName(String name) {
        return localeDataStorage.getAbilityByName(name);
    }

    @Override
    public Single<HeroWithAbility> getHeroWithAbilityByName(String name) {
        return localeDataStorage.getHeroWithAbilityByName(name);
    }

    @Override
    public Completable addHero(Hero hero) {
        return localeDataStorage.addHero(hero);
    }

    @Override
    public Completable addAbility(Ability ability) {
        return localeDataStorage.addAbility(ability);
    }

    @Override
    public void updateHero(Hero hero) {
        localeDataStorage.updateHero(hero);
    }

    @Override
    public void deleteHero(Hero hero) {
        localeDataStorage.deleteHero(hero);
    }

    @Override
    public Completable addItem(Item item) {
        return localeDataStorage.addItem(item);
    }

    private void logged(String message) {
        Log.d(TAG, message);
    }
}
