package com.srgpanov.dotaskillstimer.ui.screens.timerScreen;

import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;
import com.srgpanov.dotaskillstimer.ui.screens.BasePresenter;

public interface HeroTimerPresenterI extends BasePresenter<HeroTimerView> {
    void onItemAddClick(int positionOfHero);

    void onSelectItem(int positionOfHero, Item item);

    void onItemRemoved(String tag);

    void addHero(HeroWithAbility hero);
}
