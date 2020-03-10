package com.srgpanov.dotaskillstimer.ui.screens.settingScreen;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.ui.screens.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingContainerFragment extends Fragment {
    private MainActivity mainActivity;
    private Toolbar toolbar;
    private View fragmentView;
    private ActionBar actionBar;

    public SettingContainerFragment() {
        // Required empty public constructor
    }


    public static SettingContainerFragment newInstance(String param1, String param2) {

        return new SettingContainerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_setting_container, container, false);
        toolbar=fragmentView.findViewById(R.id.toolbar_settings);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setSupportActionBar(toolbar);
            actionBar = mainActivity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(mainActivity.getString(R.string.settings));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        setHasOptionsMenu(true);
        return fragmentView;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            mainActivity.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


}
