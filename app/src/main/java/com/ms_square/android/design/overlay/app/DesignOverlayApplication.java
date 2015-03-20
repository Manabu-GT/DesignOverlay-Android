package com.ms_square.android.design.overlay.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.ms_square.android.design.overlay.BuildConfig;
import com.ms_square.android.design.overlay.R;

import timber.log.Timber;

public class DesignOverlayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // set up Timber for logging
            Timber.plant(new Timber.DebugTree());
        }

        // set default values for the app preference
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}