package com.example.user.refreshtest;

/**
 * Created by zzj on 2017/6/21.
 */

public class RefreshIndicator {

    private float mResistance;
    private float mRatioOfHeaderHeightToRefresh;
    private int mHeaderHeight;
    private int mOffsetToRefresh;
    private int mCurrentPos;
    private int mLastPos;

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * ratio);
    }

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
        updateHeight();
    }

    private void updateHeight() {
        mOffsetToRefresh = (int) (mRatioOfHeaderHeightToRefresh * mHeaderHeight);
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    public int getLastPosY() {
        return mLastPos;
    }
}
