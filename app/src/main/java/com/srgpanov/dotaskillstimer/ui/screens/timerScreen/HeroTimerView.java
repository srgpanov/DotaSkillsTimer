package com.srgpanov.dotaskillstimer.ui.screens.timerScreen;

import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;
import com.srgpanov.dotaskillstimer.ui.screens.MvpView;

import java.util.List;

public interface HeroTimerView extends MvpView {
    void showHeroWithAbility(List<HeroWithAbility> heroes);

    void refreshAbilityCallDown(List<HeroWithAbility> heroes);

    void showItemList(int positionOfHero,List<Item> itemListNotOnHero);

    void showMessage(String message);
}
