package com.example.dotaskillstimer.ui.screens.heroListScreen;

import android.util.Log;
import android.util.SparseArray;

import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HeroListPresenterImpl implements HeroListPresenter {
    private final String TAG = "HeroListPresenterImpl";
    @Inject
    HeroRepository repository;
    private HeroListView heroListView;
    private List<HeroWithAbility> heroesAvailableForPick;
    private SparseArray<HeroWithAbility> enemyPick;
    private CompositeDisposable compDisposable = new CompositeDisposable();



    public HeroListPresenterImpl() {
        App.getInstance().getComponentsHolder().getAppComponent().injectHeroListPresenter(this);
        heroesAvailableForPick=new ArrayList<>();
        enemyPick=new SparseArray<>();
        logging("method HeroListPresenterImpl");
    }

    @Override
    public void attachView(HeroListView view) {
        logging("method attachView");
        heroListView = view;
        if (heroesAvailableForPick.size() > 0) {
            logging("attachView"+" heroesAvailableForPick.size() > 0");
            heroListView.showHeroListForPick(heroesAvailableForPick);
        } else {
            logging("attachView"+" heroesAvailableForPick.size() < 0");
            Disposable disposable = repository
                    .getAllHeroesWithAbilities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(hero -> {
                        Log.d(TAG, hero.toString());
                        heroesAvailableForPick.addAll(hero);
                        heroListView.showHeroListForPick(heroesAvailableForPick);
                    }, throwable -> Log.d(TAG, throwable.getMessage()));
            compDisposable.add(disposable);
        }
        logging("attachView"+" showHeroesEnemyPick");
        heroListView.showHeroesEnemyPick(enemyPick);
    }

    @Override
    public void onHeroPick(HeroWithAbility hero) {
        logging("method onHeroPick");
        if (enemyPick.size()>4)return;
        logging("enemyPick.size()"+enemyPick.size());
        enemyPick.put(enemyPick.size(),hero);
        int indexHeroForRemove = heroesAvailableForPick.indexOf(hero);
        heroesAvailableForPick.remove(indexHeroForRemove);
        if(indexHeroForRemove!=-1){
            heroListView.removeHeroesFromPick(indexHeroForRemove,enemyPick.size());
            heroListView.addHeroesEnemyPick(hero,enemyPick.size());
        }
    }

    @Override
    public void onEnemyPickReady() {
        logging("method onEnemyPickReady");
        heroListView.startTimerScreen(enemyPick);
    }


    @Override
    public void detachView() {
        logging("method detachView");

    }
    private void logging(String message){
        Log.d(TAG,message);
    }
}
