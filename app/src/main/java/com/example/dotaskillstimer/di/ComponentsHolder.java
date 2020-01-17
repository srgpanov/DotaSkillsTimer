package com.example.dotaskillstimer.di;

import android.content.Context;

public class ComponentsHolder {
    private Context context;
    private AppComponent appComponent;
    private HeroTimerComponent heroTimerComponent;
    private HeroListComponent heroListComponent;

    public ComponentsHolder(Context context) {
        this.context = context;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void init() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(context)).build();
    }

    public HeroTimerComponent getHeroTimerComponent() {
        if (heroTimerComponent == null) {
            heroTimerComponent = getAppComponent().createHeroTimerComponent();
        }
        return heroTimerComponent;
    }

    public void releaseHeroTimerComponent() {
        heroTimerComponent = null;
    }

    public HeroListComponent getHeroListComponent() {
        if (heroListComponent == null) {
            heroListComponent = getAppComponent().createHeroListComponent();
        }
        return heroListComponent;
    }

    public void releaseHeroListComponent() {
        heroListComponent = null;
    }
}
