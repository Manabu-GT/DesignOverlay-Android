package com.ms_square.android.design.overlay.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ms_square.android.design.overlay.R;

public class ImagePreference extends Preference {

    private ImageView mImageView;

    private Bitmap mBitmap;

    // this is the one used when inflating preference from XML
    public ImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        mImageView.setImageBitmap(mBitmap);
    }

    public void updateImage(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
        mBitmap = bitmap;
    }
}