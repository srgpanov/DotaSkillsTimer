package com.example.dotaskillstimer.di;

import android.content.Context;

import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.data.CloudFireStore;
import com.example.dotaskillstimer.data.HeroDatabase;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroRepositoryImpl;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import androidx.room.Database;
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
    HeroRepository provideHeroRepository (HeroDatabase database){
        return new HeroRepositoryImpl(database);
    }
    @Singleton
    @Provides
    CloudFireStore provideFireStore(){
        return new CloudFireStore(FirebaseFirestore.getInstance());
    }

}
