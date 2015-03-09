package com.ms_square.android.design.overlay.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ms_square.android.design.overlay.R;
import com.ms_square.android.design.overlay.activity.base.BaseActivity;
import com.ms_square.android.design.overlay.app.AppEnvironment;
import com.ms_square.android.design.overlay.event.OverlayServiceEvent;
import com.ms_square.android.design.overlay.service.DesignOverlayService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    @ViewById(R.id.grid_switch)
    Switch mGridSwitch;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity_.class);
        return intent;
    }

    @AfterViews
    void afterViews() {
        mGridSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(DesignOverlayService.createIntent(SettingsActivity.this));
                } else {
                    stopService(DesignOverlayService.createIntent(SettingsActivity.this));
                }
            }
        });
    }

    @Override
    protected boolean shouldRegisterToEventBus() {
        return true;
    }

    public void onEventMainThread(OverlayServiceEvent event) {
        if (mGridSwitch != null) {
            mGridSwitch.setChecked(event.isRunning);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGridSwitch.setChecked(AppEnvironment.INSTANCE.isOverlayServiceRunning());
    }
}