package com.srgpanov.dotaskillstimer.utils;

import com.srgpanov.dotaskillstimer.data.entity.Hero;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;

public class HeroesConverter {
    public static HeroWithAbility heroWithAbilityFromHero(Hero hero){
        HeroWithAbility heroWithAbility = new HeroWithAbility();
        heroWithAbility.setName(hero.getName());
        heroWithAbility.setAvatar(hero.getAvatar());
        heroWithAbility.setPrimaryAttributes(hero.getPrimaryAttributes());
        return heroWithAbility;
    }
}
