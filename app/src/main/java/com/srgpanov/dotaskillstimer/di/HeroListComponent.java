package com.srgpanov.dotaskillstimer.di;

import com.srgpanov.dotaskillstimer.ui.screens.heroListScreen.HeroListFragment;

import dagger.Subcomponent;

@HeroListScope
@Subcomponent(modules = HeroListModule.class)
public interface HeroListComponent {
    void inject(HeroListFragment mainFragment);
}
