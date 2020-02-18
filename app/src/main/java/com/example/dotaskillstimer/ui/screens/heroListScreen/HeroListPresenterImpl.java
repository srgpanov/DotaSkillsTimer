package com.example.dotaskillstimer.ui.screens.heroListScreen;

import android.util.Log;

import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.data.Ability;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private List<HeroWithAbility> enemyPick;
    private CompositeDisposable compDisposable = new CompositeDisposable();



    public HeroListPresenterImpl() {
        App.getInstance().getComponentsHolder().getAppComponent().injectHeroListPresenter(this);
        heroesAvailableForPick=new ArrayList<>();
        enemyPick=new ArrayList<>();
        logging("method HeroListPresenterImpl");
    }

    @Override
    public void attachView(HeroListView view) {
        logging("method attachView");
        heroListView = view;
        if (heroesAvailableForPick.size() > 0) {
            logging("attachView"+" heroesAvailableForPick.size() > 0");
            heroListView.showHeroesForPick(heroesAvailableForPick);
        } else {
            logging("attachView"+" heroesAvailableForPick.size() < 0");
            Disposable disposable = repository
                    .getAllHeroesWithAbilities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(heroes -> {
                        Log.d(TAG, heroes.toString());
                        for (HeroWithAbility heroWithAbility : heroes){
                            sortAbilityByPosition(heroWithAbility);
                        }
                        heroesAvailableForPick.addAll(heroes);
                        heroListView.showHeroesForPick(heroesAvailableForPick);
                    }, throwable -> Log.d(TAG, throwable.getMessage()));
            compDisposable.add(disposable);
        }
        logging("attachView"+" showHeroesEnemyDraft"+ heroesAvailableForPick.hashCode());
        heroListView.showHeroesEnemyDraft(enemyPick);
    }
    private void sortAbilityByPosition(HeroWithAbility heroWithAbility) {
        Collections.sort(heroWithAbility.getAbilities(), new Comparator<Ability>() {
            @Override
            public int compare(Ability o1, Ability o2) {
                return Integer.compare(o1.getPosition(),o2.getPosition());
            }
        });
    }

    @Override
    public void onHeroPick(HeroWithAbility hero) {
        logging("method onHeroPick");
        if (enemyPick.size()>4)return;
        logging("enemyPick.size()"+enemyPick.size());
        enemyPick.add(hero);
        int indexHeroForRemove = heroesAvailableForPick.indexOf(hero);
        heroesAvailableForPick.remove(indexHeroForRemove);
        if(indexHeroForRemove!=-1){
            heroListView.moveHeroFromPickToDraft(hero);
        }
        logging("heroes "+heroesAvailableForPick.size());
    }

    @Override
    public void onEnemyPickReady() {
        logging("method onEnemyPickReady");
        heroListView.startTimerScreen(enemyPick);
    }

    @Override
    public void onHeroRemoveFromPick(int pickCount) {
        HeroWithAbility  hero =enemyPick.get(pickCount);
        enemyPick.remove(pickCount);
        heroesAvailableForPick.add(hero);
        heroListView.moveHeroFromDraftToPick(hero,pickCount);
        logging("onHeroRemoveFromPick" +enemyPick);
    }


    @Override
    public void detachView() {
        logging("method detachView");
        heroListView=null;

    }
    private void logging(String message){
        Log.d(TAG,message);
    }
}
