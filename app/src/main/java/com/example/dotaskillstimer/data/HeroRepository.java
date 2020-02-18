package com.example.dotaskillstimer.data;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public interface HeroRepository {
    Flowable<List<Hero>> getAllHeroes();
    Flowable<List<Ability>> getAllAbilities();
    Flowable<List<HeroWithAbility>> getAllHeroesWithAbilities();
    Flowable<List<Item>> getAllItems();

    Maybe<Hero> getHeroByName(String name);
    Maybe<Ability> getAbilityByName(String name);

    Maybe<HeroWithAbility> getHeroWithAbilityByName(String name);
    void  addHero(Hero hero);
    void addAbility(Ability ability);
    void updateHero (Hero hero);

    void deleteHero (Hero hero);

    void addItem(Item item);
}
