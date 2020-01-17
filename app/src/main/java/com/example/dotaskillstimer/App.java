package com.example.dotaskillstimer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.dotaskillstimer.di.AppComponent;
import com.example.dotaskillstimer.di.AppModule;
import com.example.dotaskillstimer.di.ComponentsHolder;

public class App extends Application {
    private ComponentsHolder componentsHolder;
    private static App instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        componentsHolder =new ComponentsHolder(this);
        componentsHolder.init();
    }

    public static App getInstance() {

        return instance;
    }

    public ComponentsHolder getComponentsHolder() {
        return componentsHolder;
    }
}
