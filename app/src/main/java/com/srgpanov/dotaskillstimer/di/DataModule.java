package com.srgpanov.dotaskillstimer.di;

import android.content.Context;

import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.CloudFireStore;
import com.srgpanov.dotaskillstimer.data.HeroDatabase;
import com.srgpanov.dotaskillstimer.data.HeroRepository;
import com.srgpanov.dotaskillstimer.data.HeroRepositoryImpl;
import com.srgpanov.dotaskillstimer.data.LocaleDataStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Singleton
    @Provides
    HeroDatabase provideHeroDatabase(Context context){
        HeroDatabase heroDatabase = Room.databaseBuilder(context,HeroDatabase.class,context.getResources().getString(R.string.DataBaseName)).build();
        return heroDatabase;
    }
    @Singleton
    @Provides
    LocaleDataStorage provideLocaleDataStorage (HeroDatabase database){
        return new LocaleDataStorage(database);
    }
    @Singleton
    @Provides
    HeroRepository provideHeroRepository (HeroDatabase database){
        return new HeroRepositoryImpl();
    }
    @Singleton
    @Provides
    CloudFireStore provideFireStore(){
        return new CloudFireStore(FirebaseFirestore.getInstance());
    }

}
