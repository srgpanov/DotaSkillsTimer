package com.example.dotaskillstimer.ui.screens.settingScreen;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dotaskillstimer.R;

/**

 */
public class SettingFragment extends PreferenceFragmentCompat  {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences sharedPreferences;




    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.mainfile,rootKey);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        logged("countSetting"+sharedPreferences.getAll());
    }



    private void logged(String message){
        Log.d(TAG,message);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
