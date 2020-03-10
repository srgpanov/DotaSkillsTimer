package com.srgpanov.dotaskillstimer.ui.screens.heroListScreen;

import android.util.Log;

import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.HeroRepository;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
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
        heroesAvailableForPick = new ArrayList<>();
        enemyPick = new ArrayList<>();
        logging("method HeroListPresenterImpl");
    }

    @Override
    public void attachView(HeroListView view) {
        logging("method attachView");
        heroListView = view;
        showHeroesList();
        showEnemyDraft();
    }

    private void showEnemyDraft() {
        if (!enemyPick.isEmpty()) {
            heroListView.showHeroesEnemyDraft(enemyPick);
        }
    }

    private void showHeroesList() {
        if (!heroesAvailableForPick.isEmpty()) {
            logging("attachView" + " heroesAvailableForPick.size() > 0");
            heroListView.showHeroesForPick(heroesAvailableForPick);
        } else {
            logging("attachView" + " heroesAvailableForPick.isEmpty " + heroesAvailableForPick.toString());
            repository.getAllHeroesWithAbilities()
                    .filter(list -> {
                        logging("filter" + list.toString());
                        return !list.isEmpty();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MaybeObserver<List<HeroWithAbility>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compDisposable.add(d);
                            heroListView.showLoading(true);
                            logging("doOnSubscribe");
                            logging("compDisposable.isDisposed() "+compDisposable.isDisposed());
                        }

                        @Override
                        public void onSuccess(List<HeroWithAbility> heroes) {
                            logging("heroes" + heroes.toString());
                            heroListView.showLoading(false);
                            heroesAvailableForPick.addAll(heroes);
                            heroListView.showHeroesForPick(heroesAvailableForPick);
                        }

                        @Override
                        public void onError(Throwable e) {
                            logging("error " + e.getMessage());
                            heroListView.showLoading(false);
                            heroListView.showMessage(App.getInstance().getString(R.string.something_went_wrong));
                        }

                        @Override
                        public void onComplete() {
                            heroListView.showLoading(false);
                            heroListView.showMessage(App.getInstance().getString(R.string.check_internet));
                            logging("complete");
                        }
                    });
        }
    }

    @Override
    public void onHeroPick(HeroWithAbility hero) {
        if (enemyPick.size() > 4) {
            heroListView.showMessage(App.getInstance().getString(R.string.enemy_draft));
            return;
        }

        logging("enemyPick.size()" + enemyPick.size());
        enemyPick.add(hero);
        int indexHeroForRemove = heroesAvailableForPick.indexOf(hero);
        heroesAvailableForPick.remove(indexHeroForRemove);
        if (indexHeroForRemove != -1) {
            heroListView.moveHeroFromPickToDraft(hero);
        }
        logging("heroes " + heroesAvailableForPick.size());
    }

    @Override
    public void onEnemyPickReady() {
        logging("method onEnemyPickReady");
        for (HeroWithAbility hero : enemyPick) {
            hero.getItemList().clear();
        }
        heroListView.startTimerScreen(enemyPick);
    }

    @Override
    public void onHeroRemoveFromPick(int pickCount) {
        HeroWithAbility hero = enemyPick.get(pickCount);
        enemyPick.remove(pickCount);
        heroesAvailableForPick.add(hero);
        heroListView.moveHeroFromDraftToPick(hero, pickCount);
        logging("onHeroRemoveFromPick" + enemyPick);
    }

    @Override
    public void detachView() {
        logging("method detachView");
        if (compDisposable != null && !compDisposable.isDisposed()) {
            compDisposable.clear();
        }
        heroListView = null;

    }

    private void logging(String message) {
        Log.d(TAG, message);
    }
}
