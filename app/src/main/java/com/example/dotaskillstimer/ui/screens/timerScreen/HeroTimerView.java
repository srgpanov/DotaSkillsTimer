package com.example.dotaskillstimer.ui.screens.timerScreen;

import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.data.Item;
import com.example.dotaskillstimer.ui.screens.MvpView;

import java.util.List;

public interface HeroTimerView extends MvpView {
    void showHeroWithAbility(List<HeroWithAbility> heroes);

    void refreshAbilityCallDown(List<HeroWithAbility> heroes);

    void showItemList(int positionOfHero,List<Item> itemListNotOnHero);

    void showMessage(String message);
}
