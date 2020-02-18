package com.example.dotaskillstimer.di;

import com.example.dotaskillstimer.ui.ItemSelectDialogFragment;
import com.example.dotaskillstimer.ui.screens.heroListScreen.HeroListPresenterImpl;
import com.example.dotaskillstimer.ui.screens.timerScreen.HeroTimerPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class,DataModule.class})
public interface AppComponent {
    HeroTimerComponent createHeroTimerComponent();
    HeroListComponent createHeroListComponent();
    void injectHeroTimerPresenter(HeroTimerPresenter presenter);
    void injectHeroListPresenter(HeroListPresenterImpl presenter);
    void injectItemPickerDialogFragment(ItemSelectDialogFragment fragment);

}
