package com.example.dotaskillstimer.ui.screens.heroListScreen;

import android.util.SparseArray;

import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.ui.screens.MvpView;

import java.util.List;
import java.util.Map;

public interface HeroListView extends MvpView {
    void showHeroListForPick(List<HeroWithAbility>heroes);
    void showHeroesEnemyPick(SparseArray<HeroWithAbility> enemyPick);
    void removeHeroesFromPick(int position,int pickCount);
    void addHeroesEnemyPick(HeroWithAbility hero, int pickCount);

    void startTimerScreen(SparseArray<HeroWithAbility> enemyPick);
}
