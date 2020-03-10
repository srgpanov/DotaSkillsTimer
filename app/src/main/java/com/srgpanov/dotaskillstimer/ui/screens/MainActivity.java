package com.srgpanov.dotaskillstimer.ui.screens;

import android.os.Bundle;

import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.ui.MyNavigator;
import com.srgpanov.dotaskillstimer.utils.IOnBackPressed;

import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigator;

public class MainActivity extends AppCompatActivity implements MyNavigator {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        navController = androidx.navigation.Navigation.findNavController(this, R.id.nav_host_fragment);

    }

    @Override
    public void navigate(int destination, Bundle bundle, @Nullable Navigator.Extras extras) {
        navController.navigate(destination, bundle, null, extras);
    }

    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment!=null){
            List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof IOnBackPressed) {
                    ((IOnBackPressed) fragment).onBackPressed();
                }
            }
        }


        super.onBackPressed();
    }
}
