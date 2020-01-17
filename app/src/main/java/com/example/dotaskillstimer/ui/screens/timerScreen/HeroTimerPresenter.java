package com.example.dotaskillstimer.ui.screens.timerScreen;

import android.util.Log;

import com.example.dotaskillstimer.App;
import com.example.dotaskillstimer.data.CloudFireStore;
import com.example.dotaskillstimer.data.HeroRepository;
import com.example.dotaskillstimer.data.HeroWithAbility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HeroTimerPresenter implements HeroTimerPresenterI {
    private final String TAG = "HeroTimerPresenter";
    @Inject
    HeroRepository mRepository;
    @Inject
    CloudFireStore cloudStore;
    private List<HeroWithAbility> heroes = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();


    private HeroTimerView heroTimerView;

    public HeroTimerPresenter() {
        App.getInstance().getComponentsHolder().getAppComponent().injectHeroTimerPresenter(this);
    }


    @Override
    public void attachView(HeroTimerView view) {
        heroTimerView = view;
        Log.d(TAG, "attachView" + view.hashCode());
        if (heroes.isEmpty()) {
            Disposable getHeroesDis = mRepository.getAllHeroesWithAbilities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<HeroWithAbility>>() {
                        @Override
                        public void accept(List<HeroWithAbility> hero) throws Exception {
                            heroes.clear();
                            heroes.addAll(hero);
                            heroTimerView.showHeroWithAbility(hero);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d(TAG, throwable.getMessage());
                        }
                    });

            disposable.add(getHeroesDis);
        } else {
            heroTimerView.showHeroWithAbility(heroes);
        }
    }

    @Override
    public void detachView() {
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        Log.d(TAG, "detachView" + heroTimerView.hashCode());

        this.heroTimerView = null;
    }
    public void addHero(HeroWithAbility hero){
        heroes.add(hero);
    }
}
