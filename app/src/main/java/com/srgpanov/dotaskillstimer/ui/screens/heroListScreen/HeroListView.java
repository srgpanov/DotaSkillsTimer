package com.srgpanov.dotaskillstimer.ui.screens.heroListScreen;

import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.ui.screens.MvpView;

import java.util.List;

public interface HeroListView extends MvpView {
    void showHeroesForPick(List<HeroWithAbility>heroes);
    void showHeroesEnemyDraft(List<HeroWithAbility> enemyPick);
    void moveHeroFromPickToDraft(HeroWithAbility hero);

    void moveHeroFromDraftToPick(HeroWithAbility hero,int pickCount);


    void startTimerScreen(List<HeroWithAbility> enemyPick);

    void showMessage(String message);

    void showLoading(boolean show);
}
