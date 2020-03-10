package com.srgpanov.dotaskillstimer.ui;

import android.os.Bundle;

import javax.annotation.Nullable;

import androidx.navigation.Navigator;

public interface MyNavigator {
    void navigate(int resId, Bundle bundle, @Nullable Navigator.Extras extras);
}
