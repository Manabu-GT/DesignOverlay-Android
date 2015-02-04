package com.ms_square.android.design.overlay.app;

import com.ms_square.android.design.overlay.event.OverlayServiceEvent;

import de.greenrobot.event.EventBus;

public enum AppEnvironment {
    INSTANCE;

    private boolean mOverlayServiceRunning;

    public void setOverlayServiceRunning(boolean isRunning) {
        mOverlayServiceRunning = isRunning;
        EventBus.getDefault().post(new OverlayServiceEvent(isRunning));
    }

    public boolean isOverlayServiceRunning() {
        return mOverlayServiceRunning;
    }
}