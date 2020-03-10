package com.srgpanov.dotaskillstimer.di;

import com.srgpanov.dotaskillstimer.ui.screens.heroListScreen.HeroListPresenter;
import com.srgpanov.dotaskillstimer.ui.screens.heroListScreen.HeroListPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class HeroListModule {
    @HeroListScope
    @Provides
    HeroListPresenter provideHeroTimerPresenter() {
        return new HeroListPresenterImpl();
    }

}
