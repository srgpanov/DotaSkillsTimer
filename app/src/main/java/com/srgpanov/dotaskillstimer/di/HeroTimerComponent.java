package com.srgpanov.dotaskillstimer.di;

import com.srgpanov.dotaskillstimer.ui.screens.timerScreen.TimerFragment;

import dagger.Subcomponent;

@HeroTimerScope
@Subcomponent(modules = HeroTimerModule.class)
public interface HeroTimerComponent {
    void inject(TimerFragment timerFragment);
}
