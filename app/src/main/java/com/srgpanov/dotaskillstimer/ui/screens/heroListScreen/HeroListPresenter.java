package com.srgpanov.dotaskillstimer.ui.screens.heroListScreen;

import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.ui.screens.BasePresenter;

public interface HeroListPresenter extends BasePresenter<HeroListView> {
     void onHeroPick(HeroWithAbility hero);
     void onEnemyPickReady();
     void onHeroRemoveFromPick(int pickCount);
}
