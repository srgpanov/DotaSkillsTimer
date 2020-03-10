package com.srgpanov.dotaskillstimer.ui.screens.timerScreen;

import android.util.Log;

import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.data.entity.Ability;
import com.srgpanov.dotaskillstimer.data.CloudFireStore;
import com.srgpanov.dotaskillstimer.data.HeroRepository;
import com.srgpanov.dotaskillstimer.data.entity.HeroWithAbility;
import com.srgpanov.dotaskillstimer.data.entity.Item;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HeroTimerPresenter implements HeroTimerPresenterI {
    private final String TAG = "HeroTimerPresenter";
    @Inject
    HeroRepository mRepository;
    @Inject
    CloudFireStore cloudFireStore;
    private List<HeroWithAbility> heroes = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Item> allAvailableItems = new ArrayList<>();
    private HeroTimerView heroTimerView;

    public HeroTimerPresenter() {
        App.getInstance().getComponentsHolder().getAppComponent().injectHeroTimerPresenter(this);
    }

    @Override
    public void attachView(HeroTimerView view) {
        heroTimerView = view;
        Log.d(TAG, "attachView" + view.hashCode());
        if (!heroes.isEmpty()) {
            heroTimerView.showHeroWithAbility(heroes);
        }
        Disposable allItemsDisposable = mRepository.getAllItems()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Item>>() {
                    @Override
                    public void accept(List<Item> items) throws Exception {
                        logged(String.valueOf(items));
                        if (items.size() > allAvailableItems.size()) {
                            allAvailableItems.clear();
                            allAvailableItems.addAll(items);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logged("error on load items " + throwable.getMessage());
                        logged(throwable.getMessage());
                    }
                });
        disposable.add(allItemsDisposable);
    }

    @Override
    public void detachView() {
        if (disposable != null ) {
            disposable.dispose();
        }
        Log.d(TAG, "detachView" + heroTimerView.hashCode());
        this.heroTimerView = null;
    }

    public void addHero(HeroWithAbility hero) {
        if (!heroes.contains(hero)) {
            logged("items" + hero.hashCode() + " " + hero.getItemList());
            heroes.add(hero);
        }

    }

    private void logged(String message) {
        Log.d(TAG, message);
    }

    public void applyCallDawnReduction(int heroPosition, float percent) {
        heroes.get(heroPosition - 1).applyCallDownReduction(percent);
        List<Ability> abilities = heroes.get(heroPosition - 1).getAbilities();
        for (Ability ability : abilities) {
            logged("callDown" + ability.toString());
            ;
        }
        heroTimerView.refreshAbilityCallDown(heroes);
    }


    @Override
    public void onItemAddClick(int positionOfHero) {
        if (heroes.get(positionOfHero).getItemList().size() >= 6) {
            heroTimerView.showMessage("Hero cannot have more than 6 items");
        } else {
            List<Item> heroItems = heroes.get(positionOfHero).getItemList();
            List<Item> itemListNotOnHero = new ArrayList<>(allAvailableItems);
            for (Item item : heroItems) {
                itemListNotOnHero.remove(item);
            }
            heroTimerView.showItemList(positionOfHero, itemListNotOnHero);
        }
    }

    @Override
    public void onSelectItem(int positionOfHero, Item item) {
        heroes.get(positionOfHero).getItemList().add(item);
    }

    @Override
    public void onItemRemoved(String tag) {
        for (HeroWithAbility hero : heroes) {
            for (Ability ability : hero.getAbilities()) {
                if (ability.getName().equals(tag)) {
                    hero.getAbilities().remove(ability);
                    logged("ability " + ability + " was removed");
                    return;
                }
            }
            for (Item item : hero.getItemList()) {
                if (item.getName().equals(tag)) {
                    hero.getItemList().remove(item);
                    logged("item " + item + " was removed");
                    return;
                }
            }
        }
    }


}

