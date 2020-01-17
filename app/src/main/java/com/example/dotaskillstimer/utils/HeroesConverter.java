package com.example.dotaskillstimer.utils;

import com.example.dotaskillstimer.data.Hero;
import com.example.dotaskillstimer.data.HeroWithAbility;

public class HeroesConverter {
    public static HeroWithAbility heroWithAbilityFromHero(Hero hero){
        HeroWithAbility heroWithAbility = new HeroWithAbility();
        heroWithAbility.setName(hero.getName());
        heroWithAbility.setAvatar(hero.getAvatar());
        heroWithAbility.setPrimaryAttributes(hero.getPrimaryAttributes());
        return heroWithAbility;
    }
}
