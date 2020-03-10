package com.srgpanov.dotaskillstimer.di;

import com.srgpanov.dotaskillstimer.ui.screens.timerScreen.HeroTimerPresenter;
import com.srgpanov.dotaskillstimer.ui.screens.timerScreen.HeroTimerPresenterI;

import dagger.Module;
import dagger.Provides;

@Module
public class HeroTimerModule {
    @HeroTimerScope
    @Provides
    HeroTimerPresenterI provideHeroTimerPresenter(){
        return new HeroTimerPresenter();
    }

}
