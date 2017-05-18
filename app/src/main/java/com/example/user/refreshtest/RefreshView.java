package com.example.user.refreshtest;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Created by zzj on 2017/5/16.
 */

public class RefreshView extends ViewGroup {

    private PointF mLastPoint = new PointF();
    private boolean mPreventForHorizontal;
    private boolean mAllowHorizontalScroll;
    private View mHeaderView;
    private View mContentView;


    public RefreshView(Context context) {
        super(context);
        initViews();
    }



    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContentView.layout(l,t,r,b);
    }

    @Override
    protected void onFinishInflate() {
        mContentView = getChildAt(0);


        super.onFinishInflate();
    }

    public boolean dispatchTouchEventSuper(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreventForHorizontal = false;
                mAllowHorizontalScroll = false;
                mLastPoint.set(ev.getX(),ev.getY());
                dispatchTouchEventSuper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                float offsetX = ev.getX() - mLastPoint.x;
                float offsetY = ev.getY() - mLastPoint.y;
                mLastPoint.set(ev.getX(),ev.getY());
                if (Math.abs(offsetX) > Math.abs(offsetY)){
                    mPreventForHorizontal = true;
                    mAllowHorizontalScroll = true;
                }
                if (mAllowHorizontalScroll){
                    return dispatchTouchEventSuper(ev);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;

                //判断是否开始显示刷新头,如果可以下拉不做处理
                if (moveDown && checkCanDoRefresh(this,mContentView,mHeaderView)){
                    return dispatchTouchEventSuper(ev);
                }
                //到顶下拉或者up且已经处于下拉状态，才可以移动contentView
                if (moveDown){
                    moveUp(offsetY);
                    return true;
                }

                break;
        }

        checkCanDoRefresh(this,mContentView,mHeaderView);

        return dispatchTouchEventSuper(ev);
    }
    int currentPosY = 0;
    private void moveUp(float offsetY) {

        if (offsetY < 0 /*向上滑动且contentView在起始位置*/){
            return;
        }
        int to = (int) (currentPosY + offsetY);

        if (to < 0){
            to = 0;
        }

        int change = to - currentPosY;
        currentPosY =  to;
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) return;
        mHeaderView.offsetTopAndBottom(change);
//        if (!isPinContent()) {
            mContentView.offsetTopAndBottom(change);
//        }
        invalidate();
    }

    /**
     * 是否可以下拉
     * @param refreshView
     * @param mContentView
     * @param mHeaderView
     * @return
     */
    private boolean checkCanDoRefresh(RefreshView refreshView, View mContentView, View mHeaderView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
        if (mContentView instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mContentView;
            int childCount = absListView.getChildCount();
            int visiblePosition = absListView.getFirstVisiblePosition();
            int top = 0;
            if (absListView.getChildAt(0) != null) {
                top = absListView.getChildAt(0).getTop();
            }
            int paddingTop = absListView.getPaddingTop();
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        } else {
            int scrollY = mContentView.getScrollY();
            return mContentView.getScrollY() > 0;
        }
        } else {
            return mContentView.canScrollVertically(-1);
        }
    }

    public void setHeader(View header){
        mHeaderView = header;
    }
}
