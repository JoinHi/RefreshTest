package com.example.user.refreshtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by zzj on 2017/6/21.
 */

public class RefreshLayout extends ViewGroup {


    private static int ID = 1;
    protected final String LOG_TAG = "ref-frame-" + ++ID;
    private RefreshIndicator mRefreshIndicator;
    private int mHeaderId;
    private int mContainerId;
    private int mDurationToClose;
    private int mDurationToCloseHeader;
    private boolean mKeepHraderWhenRefresh;
    private boolean mPullToRefresh;
    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private View mHeaderView;
    private View mContent;
    private int mHeaderHeight;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRefreshIndicator = new RefreshIndicator();

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout, 0, 0);

        if (arr != null) {
            mHeaderId = arr.getResourceId(R.styleable.RefreshLayout_ref_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.RefreshLayout_ref_content, mContainerId);
            mRefreshIndicator.setResistance(arr.getFloat(R.styleable.RefreshLayout_ref_resistance, mRefreshIndicator.getResistance()));
            mDurationToClose = arr.getInt(R.styleable.RefreshLayout_ref_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.RefreshLayout_ref_duration_to_close_heander, mDurationToCloseHeader);

            float ratio = mRefreshIndicator.getRatioOfHeaderToHeightRefresh();
            ratio = arr.getFloat(R.styleable.RefreshLayout_ref_ratio_of_header_height_to_refresh, ratio);
            mRefreshIndicator.setRatioOfHeaderHeightToRefresh(ratio);

            mKeepHraderWhenRefresh = arr.getBoolean(R.styleable.RefreshLayout_ref_keep_header_when_refresh, mKeepHraderWhenRefresh);

            mPullToRefresh = arr.getBoolean(R.styleable.RefreshLayout_ref_pull_to_refresh, mPullToRefresh);
            arr.recycle();
        }

        mScrollChecker = new ScrollChecker();
        ViewConfiguration conf = ViewConfiguration.get(context);
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalArgumentException("RefreshFrameLayout can only contains 2 children");
        } else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            if (mContent == null || mHeaderView == null) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof RefUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof RefUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    if (mHeaderView == null && mContent == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    } else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in RefreshFramelayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }

        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(LOG_TAG, String.format("onMeasure frame: width: %s, height: %s, padding: %s %s %s %s",
                getMeasuredHeight(), getMeasuredWidth(),
                getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom()));

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mRefreshIndicator.setHeaderHeight(mHeaderHeight);
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);

            ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            Log.d(LOG_TAG, String.format("onMeasure content, width: %s, height: %s, margin: %s %s %s %s",
                    getMeasuredWidth(), getMeasuredHeight(),
                    lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin));
            Log.d(LOG_TAG, String.format("onMeasure, currentPos: %s, lastPos: %s, top: %s",
                    mRefreshIndicator.getCurrentPosY(), mRefreshIndicator.getLastPosY(), mContent.getTop()));
        }
    }

    private void measureContentView(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {

    }

    private class ScrollChecker {
        public void destroy() {

        }
    }
}
