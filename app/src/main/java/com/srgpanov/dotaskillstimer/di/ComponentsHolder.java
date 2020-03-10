package com.srgpanov.dotaskillstimer.di;

import android.content.Context;
import android.util.Log;

public class ComponentsHolder {
    private final String TAG = this.getClass().getSimpleName();
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
        logged("create HeroTimerComponent");
        if (heroTimerComponent == null) {
            heroTimerComponent = getAppComponent().createHeroTimerComponent();
            logged("HeroTimerComponent created");
        }

        return heroTimerComponent;
    }

    public void releaseHeroTimerComponent() {
        logged("releaseHeroTimerComponent");
        heroTimerComponent = null;
    }

    public HeroListComponent getHeroListComponent() {
        logged("create HeroListComponent");
        if (heroListComponent == null) {
            heroListComponent = getAppComponent().createHeroListComponent();
            logged("HeroListComponent created");
        }
        return heroListComponent;
    }

    public void releaseHeroListComponent() {
        logged("releaseHeroListComponent");
        heroListComponent = null;
    }
    private void logged(String message) {
        Log.d(TAG, message);
    }
}
