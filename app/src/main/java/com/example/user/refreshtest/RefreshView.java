package com.example.user.refreshtest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zzj on 2017/5/16.
 */

public class RefreshView extends ViewGroup {
    public RefreshView(Context context) {
        super(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onFinishInflate() {
        View view = getChildAt(0);



        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
