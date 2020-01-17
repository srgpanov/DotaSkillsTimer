package com.example.dotaskillstimer.di;

import android.app.Application;
import android.content.Context;

import com.example.dotaskillstimer.data.HeroRepositoryImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext(){
        return mContext;
    }


}
