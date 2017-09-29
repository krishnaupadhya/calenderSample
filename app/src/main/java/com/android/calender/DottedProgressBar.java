package com.android.calender;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;


public class DottedProgressBar extends LinearLayout {

    private static final String TAG = "CJRDottedProgressBar";
    private int mDotMargin = 6;
    private int mDotSize = 8;
    private int mNoOfDots = 3;
    private long mJumpingSpeed = 300;
    private Handler mHandler;
    private int mActiveDotIndex = 0;
    private boolean isInProgress = false;


    public DottedProgressBar(Context context) {
        super(context);
        initViews();
    }

    public DottedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DottedProgressBar);
        if (typedArray != null) {
            int size = typedArray.getInteger(R.styleable.DottedProgressBar_dot_size, -1);
            if (size != -1) {
                mDotSize = size;
            }

            int dotMargin = typedArray.getInteger(R.styleable.DottedProgressBar_dot_margin, -1);
            if (dotMargin != -1) {
                mDotMargin = dotMargin;
            }
        }
        initViews();
    }

    public DottedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public DottedProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isInProgress) {
                try {
//                    CJRAppUtility.log(TAG, "Runnable : run");
                    mActiveDotIndex = (mActiveDotIndex + 1) % getChildCount();
                    for (int i = 0; i < getChildCount(); i++) {
                        getChildAt(i).setSelected(false);
                    }
                    getChildAt(mActiveDotIndex).setSelected(true);
                    mHandler.postDelayed(mRunnable, mJumpingSpeed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void initViews() {
        mHandler = new Handler();
        setOrientation(LinearLayout.HORIZONTAL);
        int dotSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDotSize, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDotMargin, getResources().getDisplayMetrics());

        for (int i = 0; i < mNoOfDots; i++) {
            View view = new View(getContext());
            LayoutParams params = new LayoutParams(dotSize, dotSize);
            params.setMargins(margin, margin, margin, margin);
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.dotted_progress_bar_item_bg);
            addView(view);
        }

    }

    public void startProgress() {
        try {
            isInProgress = true;

            mActiveDotIndex = -1;
            mHandler.removeCallbacks(mRunnable);
            mHandler.post(mRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopProgress() {
        try {
            isInProgress = false;

            mHandler.removeCallbacks(mRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
