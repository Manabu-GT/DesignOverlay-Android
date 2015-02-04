package com.ms_square.android.design.overlay.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.ms_square.android.design.overlay.BuildConfig;
import com.ms_square.android.design.overlay.R;

import io.fabric.sdk.android.Fabric;

public class DesignOverlayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // set default values for the app preference
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
    }
}