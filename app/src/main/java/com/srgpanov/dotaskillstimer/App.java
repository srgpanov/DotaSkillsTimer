package com.srgpanov.dotaskillstimer;

import android.app.Application;

import com.srgpanov.dotaskillstimer.di.ComponentsHolder;

public class App extends Application {
    private ComponentsHolder componentsHolder;
    private static App instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        componentsHolder =new ComponentsHolder(this);
        componentsHolder.init();

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

    public static App getInstance() {

        return instance;
    }

    public ComponentsHolder getComponentsHolder() {
        return componentsHolder;
    }
}
