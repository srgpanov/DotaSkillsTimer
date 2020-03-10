package com.srgpanov.dotaskillstimer.ui.screens;

public interface BasePresenter<V extends MvpView> {
    void attachView (V view);
    void detachView();
}
