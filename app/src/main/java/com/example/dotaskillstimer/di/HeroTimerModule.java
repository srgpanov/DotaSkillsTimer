package com.example.dotaskillstimer.di;

import com.example.dotaskillstimer.ui.screens.timerScreen.HeroTimerPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class HeroTimerModule {
    @HeroTimerScope
    @Provides
    HeroTimerPresenter provideHeroTimerPresenter(){
        return new HeroTimerPresenter();
    }

}
