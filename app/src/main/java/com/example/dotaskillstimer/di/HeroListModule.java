package com.example.dotaskillstimer.di;

import com.example.dotaskillstimer.ui.screens.heroListScreen.HeroListPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class HeroListModule {
    @HeroListScope
    @Provides
    HeroListPresenterImpl provideHeroTimerPresenter() {
        return new HeroListPresenterImpl();
    }

}
