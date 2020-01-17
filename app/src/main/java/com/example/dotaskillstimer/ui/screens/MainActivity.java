package com.example.dotaskillstimer.ui.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import android.os.Bundle;

import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.ui.Navigatator;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements Navigatator {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        navController= Navigation.findNavController(this,R.id.nav_host_fragment);
    }


    @Override
    public void navigate(int destination, Bundle bundle, @Nullable Navigator.Extras extras) {
        navController.navigate(destination,bundle,null,extras);
    }
}
