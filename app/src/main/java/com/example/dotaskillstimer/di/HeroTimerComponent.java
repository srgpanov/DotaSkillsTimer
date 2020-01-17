package com.example.dotaskillstimer.di;

import com.example.dotaskillstimer.ui.screens.timerScreen.MainFragment;

import dagger.Subcomponent;

@HeroTimerScope
@Subcomponent(modules = HeroTimerModule.class)
public interface HeroTimerComponent {
    void inject(MainFragment mainFragment);
}
