package com.ms_square.android.design.overlay.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ms_square.android.design.overlay.R;
import com.ms_square.android.design.overlay.activity.SettingsActivity;
import com.ms_square.android.design.overlay.app.AppEnvironment;
import com.ms_square.android.design.overlay.util.PrefUtil;
import com.ms_square.android.design.overlay.view.GridView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class DesignOverlayService extends Service {

    private static final int NOTIFICATION_ID = 10000;

    private static final String ACTION_DISMISS = "com.ms_square.android.design.overlay.ACTION_DISMISS";

    private WindowManager mWindowManager;

    private NotificationManager mNotificationManager;

    private View mRootView;

    private ImageView mDesignImgView;

    private GridView mGridView;

    public static Intent createIntent(Context context) {
        return new Intent(context, DesignOverlayService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppEnvironment.INSTANCE.setOverlayServiceRunning(true);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        showOverlay();

        registerReceiver(mReceiver, new IntentFilter(ACTION_DISMISS));

        PrefUtil.registerOnSharedPreferenceChangeListener(this, mPrefListener);

        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        AppEnvironment.INSTANCE.setOverlayServiceRunning(false);
        PrefUtil.unregisterOnSharedPreferenceChangeListener(this, mPrefListener);
        unregisterReceiver(mReceiver);
        dismissOverlay();
        cancelNotification();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showOverlay() {
        mRootView = LayoutInflater.from(this).inflate(R.layout.service_design_overlay, null, false);
        mDesignImgView = (ImageView) mRootView.findViewById(R.id.design_image_view);
        mGridView = (GridView) mRootView.findViewById(R.id.grid_view);
        updateImageVisibility();
        updateImageAlpha();
        updateImage();
        updateGridSize();
        updateGridColor();
        updateGridVisibility();
        mWindowManager.addView(mRootView, createDefaultSystemWindowParams(PrefUtil.isFullScreen(this)));
    }

    private void dismissOverlay() {
        mWindowManager.removeView(mRootView);
        mRootView = null;
        mDesignImgView = null;
        mGridView = null;
    }

    private void updateImage() {
        if (mDesignImgView != null) {
            final Uri uri = PrefUtil.getDesignImageUri(this);
            if (uri != null) {
                new AsyncTask<Uri, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Uri... params) {
                        Bitmap bitmap = null;
                        InputStream stream = null;
                        try {
                            stream = getContentResolver().openInputStream(params[0]);
                            bitmap = BitmapFactory.decodeStream(stream);
                        } catch (FileNotFoundException fe) {
                            Timber.w("File was not found:" + fe);
                        } finally {
                            try {
                                if (stream != null) {
                                    stream.close();
                                }
                            } catch (IOException ignore) {}
                        }
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (mDesignImgView != null) {
                            mDesignImgView.setImageBitmap(bitmap);
                        }
                    }
                }.execute(uri);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateImageAlpha() {
        if (mDesignImgView != null) {
            final int alpha = PrefUtil.getDesignImageAlpha(this); // 0 - 255
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mDesignImgView.setImageAlpha(alpha);
            } else {
                mDesignImgView.setAlpha(alpha);
            }
        }
    }

    private void updateImageVisibility() {
        if (mDesignImgView != null) {
            mDesignImgView.setVisibility(PrefUtil.isDesignImageEnabled(DesignOverlayService.this) ?
                    View.VISIBLE : View.INVISIBLE);
        }
    }

    private void updateGridSize() {
        if (mGridView != null) {
            mGridView.updateGridSize(PrefUtil.getGridSize(DesignOverlayService.this));
        }
    }

    private void updateGridColor() {
        if (mGridView != null) {
            mGridView.updateGridColor(PrefUtil.getGridColor(DesignOverlayService.this));
        }
    }

    private void updateGridVisibility() {
        if (mGridView != null) {
            mGridView.setVisibility(PrefUtil.isGridEnabled(DesignOverlayService.this) ?
                    View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_big_text)))
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_small_text))
                .setContentIntent(getNotificationIntent(null));

        mBuilder.addAction(R.drawable.ic_action_clear, getString(R.string.notification_action_dismiss),
                getNotificationIntent(ACTION_DISMISS));

        // show the notification
        startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent getNotificationIntent(String action) {
        if (action == null) {
            Intent intent = SettingsActivity.createIntent(this);
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            Intent intent = new Intent(action);
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_DISMISS:
                    stopSelf();
                    break;
            }
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PrefUtil.PREF_FULLSCREEN: {
                    dismissOverlay();
                    showOverlay();
                    break;
                }
                case PrefUtil.PREF_DESIGN_IMAGE_ENABLED: {
                    updateImageVisibility();
                    break;
                }
                case PrefUtil.PREF_DESIGN_IMAGE_URI: {
                    updateImage();
                    break;
                }
                case PrefUtil.PREF_DESIGN_IMAGE_ALPHA: {
                    updateImageAlpha();
                    break;
                }
                case PrefUtil.PREF_GRID_ENABLED: {
                    updateGridVisibility();
                    break;
                }
                case PrefUtil.PREF_GRID_SIZE: {
                    updateGridSize();
                    break;
                }
                case PrefUtil.PREF_GRID_COLOR: {
                    updateGridColor();
                    break;
                }
            }
        }
    };

    private static WindowManager.LayoutParams createDefaultSystemWindowParams(boolean isFullScreen) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                isFullScreen ? WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN : 0,
                PixelFormat.TRANSLUCENT);
        params.format = PixelFormat.RGBA_8888;
        return params;
    }
}