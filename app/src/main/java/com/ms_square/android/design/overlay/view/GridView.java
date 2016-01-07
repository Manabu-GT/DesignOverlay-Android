package com.ms_square.android.design.overlay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ms_square.android.design.overlay.R;
import com.ms_square.android.util.DimenUtil;

import timber.log.Timber;

public class GridView extends View {

    private final Paint mPaint = new Paint();

    private int mGridSize;

    private float[] mPoints;
    private boolean mAlignBottom;
    private boolean mAlignRight;

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float defaultLineWidth = DimenUtil.convertToPixelFromDip(context, 1f); // 1dp

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridView);
        mPaint.setColor(typedArray.getColor(R.styleable.GridView_lineColor, 0x7732cd32));
        mPaint.setStrokeWidth(typedArray.getDimension(R.styleable.GridView_lineWidth, defaultLineWidth));
        typedArray.recycle();

        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     *
     * @param newGridSize - in pixels
     */
    public void updateGridSize(int newGridSize, boolean alignRight, boolean alignBottom) {
        mGridSize = newGridSize;
        mAlignRight = alignRight;
        mAlignBottom = alignBottom;

        updateGrid(getWidth(), getHeight());
        invalidate();
    }

    public void updateGridColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    private void updateGrid(int width, int height) {
        int numHorizontalLines = height / mGridSize;
        int numVerticalLines = width / mGridSize;

        int numHorizontalPoints = numHorizontalLines > 0 ? (numHorizontalLines + 1) * 4 : 0;
        int numVerticalPoints =  numVerticalLines > 0 ? (numVerticalLines + 1) * 4 : 0;

        if (numHorizontalPoints + numVerticalPoints > 0) {
            mPoints = new float[numHorizontalPoints + numVerticalPoints];

            int positionShift = 0;
            if (mAlignBottom) {
                positionShift = - (mGridSize - height % mGridSize);
            }

            // set up horizontal lines
            float gap = mGridSize;
            for (int i = 0; i <= numHorizontalLines; i++) {
                int base = i * 4;
                mPoints[base] = 0f;
                mPoints[base + 1] = gap + positionShift;
                mPoints[base + 2] = (float) width;
                mPoints[base + 3] = gap + positionShift;
                gap = gap + mGridSize;
            }

            positionShift = 0;
            if (mAlignRight) {
                positionShift = - (mGridSize - width % mGridSize);
            }

            // set up vertical lines
            gap = mGridSize;
            for (int i = 0; i <= numVerticalLines; i++) {
                int base = i * 4 + numHorizontalPoints;
                mPoints[base] = gap + positionShift;
                mPoints[base + 1] = 0f;
                mPoints[base + 2] = gap + positionShift;
                mPoints[base + 3] = (float) height;
                gap = gap + mGridSize;
            }
        } else {
            mPoints = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Timber.d("SizeChanged: %d, %d, %d, %d", w, h, oldw, oldh);
        updateGrid(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPoints != null) {
            canvas.drawLines(mPoints, mPaint);
        }
    }
}