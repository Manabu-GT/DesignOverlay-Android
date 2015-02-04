package com.ms_square.android.design.overlay.activity.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends ActionBarActivity {

    private boolean mVisible;
    private boolean mStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (shouldRegisterToEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVisible = true;
        mStopped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVisible = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStopped = true;
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public boolean isVisible() {
        return mVisible;
    }

    public boolean isStopped() {
        return mStopped;
    }

    protected boolean shouldRegisterToEventBus() {
        return false;
    }
}