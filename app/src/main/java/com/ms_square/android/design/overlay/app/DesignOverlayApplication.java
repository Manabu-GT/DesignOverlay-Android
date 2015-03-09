package com.ms_square.android.design.overlay.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.ms_square.android.design.overlay.R;

public class DesignOverlayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // set default values for the app preference
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}