package com.srgpanov.dotaskillstimer.ui.screens.settingScreen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.srgpanov.dotaskillstimer.App;
import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.data.CloudFireStore;
import com.srgpanov.dotaskillstimer.data.HeroRepository;
import com.srgpanov.dotaskillstimer.utils.Constants;

import javax.inject.Inject;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class SettingFragment extends PreferenceFragmentCompat {
    private final String TAG = this.getClass().getSimpleName();
    @Inject
    CloudFireStore cloudFireStore;
    @Inject
    HeroRepository repository;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        App.getInstance().getComponentsHolder().getAppComponent().injectSettings(this);
        setPreferencesFromResource(R.xml.mainfile, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        logged("countSetting" + sharedPreferences.getAll());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        logged(preference.getKey());
        if (preference.getKey().equals(requireActivity().getString(R.string.refresh_db))) {
            checkVersionDB();
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void logged(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkVersionDB() {
        logged("checkVersionDB");
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
        long currentVersion = preferences.getLong(Constants.versionDB, 0);
        logged("currentVersion " + currentVersion);
        Single.fromCallable(() -> cloudFireStore.checkUpdate(currentVersion))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean needUpdate) {
                        logged("needUpdate " + needUpdate);
                        preferences.edit().putBoolean(Constants.isLatestVersionDB, needUpdate).apply();
                        if (needUpdate) {
                            refreshDB()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            if(isResumed())showMessage(getString(R.string.data_was_refreshed));
                                            logged("DB is refreshed");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if(isResumed())showMessage(getString(R.string.something_went_wrong));
                                            logged("DB is not refreshed");
                                        }
                                    });
                            ;
                        } else {
                            if(isResumed())showMessage(getString(R.string.data_is_actual));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(isResumed())showMessage(getString(R.string.something_went_wrong));
                    }
                });

    }

    private Completable refreshDB() {
        return repository.refreshDB();
    }

    private void showMessage(String message) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
