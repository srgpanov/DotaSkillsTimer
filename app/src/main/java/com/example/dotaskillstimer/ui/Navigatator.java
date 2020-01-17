package com.example.dotaskillstimer.ui;

import android.os.Bundle;

import javax.annotation.Nullable;

import androidx.navigation.Navigation;
import androidx.navigation.Navigator;

public interface Navigatator {
    void navigate(int resId, Bundle bundle, @Nullable Navigator.Extras extras);
}
