package com.ms_square.android.design.overlay.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.Toast;

import com.ms.square.android.util.AppUtil;
import com.ms.square.android.util.ToastMaster;
import com.ms_square.android.design.overlay.BuildConfig;
import com.ms_square.android.design.overlay.R;
import com.ms_square.android.design.overlay.task.SafeAsyncTask;
import com.ms_square.android.design.overlay.util.ImageUtil;
import com.ms_square.android.design.overlay.util.PrefUtil;
import com.ms_square.android.design.overlay.view.ImagePreference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final int REQUEST_CODE_IMAGE = 10000;

    private Context mAppContext;

    private ImagePreference mImagePreference;

    private int mImageSize;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(PrefUtil.PREF_GRID_SIZE));

        mAppContext = getActivity().getApplicationContext();

        // get listPreferredItemHeight value in pixel and set it to mImageSize
        TypedValue value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
        mImageSize = (int) value.getDimension(getResources().getDisplayMetrics());

        mImagePreference = (ImagePreference) findPreference(PrefUtil.PREF_DESIGN_IMAGE_URI);
        mImagePreference.setOnPreferenceClickListener(this);
        // load image if already set
        Uri imageUri = PrefUtil.getDesignImageUri(mAppContext);
        if (imageUri != null) {
            loadDesignImage(imageUri);
        }

        // Set application version
        Preference appVer = findPreference("pref_app_version");
        appVer.setSummary(AppUtil.getVersion(mAppContext) + " - " + BuildConfig.BUILD_NUMBER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                final Uri uri = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // needs to take the persistable permission for post kitkat devices
                    mAppContext.getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                loadDesignImage(uri);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (PrefUtil.PREF_DESIGN_IMAGE_URI.equals(preference.getKey())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.intent_chooser_choose_image)), REQUEST_CODE_IMAGE);
            }
            return true;
        }
        return false;
    }

    private void loadDesignImage(final Uri uri) {
        new SafeAsyncTask<Uri, Void, Bitmap>(getActivity()) {
            @Override
            protected Bitmap onRun(Uri... params) {
                Bitmap bitmap = null;
                InputStream stream = null;
                try {
                    stream = mAppContext.getContentResolver().openInputStream(params[0]);
                    bitmap = ImageUtil.decodeSampledBitmapFromStream(stream, mImageSize, mImageSize);
                } catch (FileNotFoundException fe) {
                    Timber.w("File was not found: %s", fe.toString());
                } catch (SecurityException se) {
                    Timber.w("Probably no longer have access permission to the uri: %s", se.toString());
                    // clear stored image Uri
                    PrefUtil.setDesignImageUri(mAppContext, null);
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
            protected void onSuccess(Bitmap bitmap) {
                if (bitmap != null) {
                    PrefUtil.setDesignImageUri(mAppContext, uri);
                    mImagePreference.updateImage(bitmap);
                } else {
                    ToastMaster.showToast(mAppContext, getString(R.string.toast_bitmap_not_found), Toast.LENGTH_LONG);
                }
            }
        }.execute(uri);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }

            return true;
        }
    };
}