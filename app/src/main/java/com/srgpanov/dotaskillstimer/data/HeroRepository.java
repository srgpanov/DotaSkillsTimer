package com.srgpanov.dotaskillstimer.data;

import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface HeroRepository {
    Single<List<Hero>> getAllHeroes();
    Single<List<Ability>> getAllAbilities();
    Single<List<HeroWithAbility>> getAllHeroesWithAbilities();
    Single<List<Item>> getAllItems();

    Single<Hero> getHeroByName(String name);
    Single<Ability> getAbilityByName(String name);

    Single<HeroWithAbility> getHeroWithAbilityByName(String name);
    Completable addHero(Hero hero);
    Completable addAbility(Ability ability);
    Completable addItem(Item item);
    void updateHero (Hero hero);

    void deleteHero (Hero hero);

    Completable refreshDB();
}
