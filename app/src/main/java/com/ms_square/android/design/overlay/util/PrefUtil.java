package com.ms_square.android.design.overlay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.ms.square.android.util.UIUtil;

public class PrefUtil {

    public static final String PREF_FULLSCREEN = "pref_fullscreen";

    public static final String PREF_DESIGN_IMAGE_ENABLED = "pref_design_image_enabled";

    public static final String PREF_DESIGN_IMAGE_URI = "pref_design_image_uri";

    public static final String PREF_DESIGN_IMAGE_ALPHA = "pref_design_image_alpha";

    public static final String PREF_GRID_ENABLED = "pref_grid_enabled";

    public static final String PREF_GRID_SIZE = "pref_grid_size";

    public static final String PREF_GRID_COLOR = "pref_grid_color";

    /** Long indicating when a preference was last updated */
    private static final String PREF_TIME_STAMP = "pref_time_stamp";

    public static boolean isFullScreen(Context context) {
        return getSharedPrefs(context).getBoolean(PREF_FULLSCREEN, false);
    }

    public static boolean isDesignImageEnabled(Context context) {
        return getSharedPrefs(context).getBoolean(PREF_DESIGN_IMAGE_ENABLED, true);
    }

    public static Uri getDesignImageUri(Context context) {
        String uriString = getSharedPrefs(context).getString(PREF_DESIGN_IMAGE_URI, null);
        if (uriString != null) {
            return Uri.parse(uriString);
        }
        return null;
    }

    public static void setDesignImageUri(Context context, Uri uri) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREF_DESIGN_IMAGE_URI, uri.toString());
        apply(editor);
    }

    public static int getDesignImageAlpha(Context context) {
        return getSharedPrefs(context).getInt(PREF_DESIGN_IMAGE_ALPHA, 100);
    }

    public static boolean isGridEnabled(Context context) {
        return getSharedPrefs(context).getBoolean(PREF_GRID_ENABLED, true);
    }

    public static int getGridSize(Context context) {
        return (int) UIUtil.convertToPixelFromDip(context,
                Float.parseFloat(getSharedPrefs(context).getString(PREF_GRID_SIZE, "4")));
    }

    public static int getGridColor(Context context) {
        return getSharedPrefs(context).getInt(PREF_GRID_COLOR, 0xff32cd32);
    }

    public static void registerOnSharedPreferenceChangeListener(Context context,
                                                                SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPrefs(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(Context context,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPrefs(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrefs(context).edit();
    }

    // if you do not care about the result and calling from the main thread
    private static void apply(SharedPreferences.Editor editor) {
        editor.putLong(PREF_TIME_STAMP, getCurrentTime());
        editor.apply();
    }

    private static void commit(SharedPreferences.Editor editor) {
        editor.putLong(PREF_TIME_STAMP, getCurrentTime());
        editor.commit();
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }
}