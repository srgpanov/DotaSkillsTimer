package com.srgpanov.dotaskillstimer.di;

import com.srgpanov.dotaskillstimer.data.HeroRepositoryImpl;
import com.srgpanov.dotaskillstimer.ui.ItemSelectDialogFragment;
import com.srgpanov.dotaskillstimer.ui.screens.heroListScreen.HeroListPresenterImpl;
import com.srgpanov.dotaskillstimer.ui.screens.settingScreen.SettingFragment;
import com.srgpanov.dotaskillstimer.ui.screens.timerScreen.HeroTimerPresenter;

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
    void injectDataStorage(HeroRepositoryImpl repository);
    void injectSettings(SettingFragment  fragment);

}
