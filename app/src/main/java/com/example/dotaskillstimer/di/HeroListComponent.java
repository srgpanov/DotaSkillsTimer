package com.example.dotaskillstimer.di;

import com.example.dotaskillstimer.ui.screens.heroListScreen.HeroListFragment;

import dagger.Subcomponent;

@HeroListScope
@Subcomponent(modules = HeroListModule.class)
public interface HeroListComponent {
    void inject(HeroListFragment mainFragment);
}
