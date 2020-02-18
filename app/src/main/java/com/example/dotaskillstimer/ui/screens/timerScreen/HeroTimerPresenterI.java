package com.example.dotaskillstimer.ui.screens.timerScreen;

import com.example.dotaskillstimer.data.Item;
import com.example.dotaskillstimer.ui.screens.BasePresenter;
import com.example.dotaskillstimer.ui.screens.timerScreen.HeroTimerView;

public interface HeroTimerPresenterI extends BasePresenter<HeroTimerView> {
    void onItemAddClick(int positionOfHero);

    void onSelectItem(int positionOfHero, Item item);

    void onItemRemoved(String tag);
}
