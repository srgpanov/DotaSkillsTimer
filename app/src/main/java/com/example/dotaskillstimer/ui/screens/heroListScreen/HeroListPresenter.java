package com.example.dotaskillstimer.ui.screens.heroListScreen;

import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.ui.screens.BasePresenter;

public interface HeroListPresenter extends BasePresenter<HeroListView> {
     void onHeroPick(HeroWithAbility hero);
     void onEnemyPickReady();
     void onHeroRemoveFromPick(int pickCount);
}
