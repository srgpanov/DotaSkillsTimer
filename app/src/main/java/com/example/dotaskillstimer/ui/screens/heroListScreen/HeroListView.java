package com.example.dotaskillstimer.ui.screens.heroListScreen;

import android.util.SparseArray;

import com.example.dotaskillstimer.data.HeroWithAbility;
import com.example.dotaskillstimer.ui.screens.MvpView;

import java.util.List;

public interface HeroListView extends MvpView {
    void showHeroesForPick(List<HeroWithAbility>heroes);
    void showHeroesEnemyDraft(List<HeroWithAbility> enemyPick);
    void moveHeroFromPickToDraft(HeroWithAbility hero);

    void moveHeroFromDraftToPick(HeroWithAbility hero,int pickCount);


    void startTimerScreen(List<HeroWithAbility> enemyPick);
}
