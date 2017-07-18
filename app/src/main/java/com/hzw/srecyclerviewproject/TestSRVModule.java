package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.content.res.Resources;

import com.hzw.srecyclerview.AbsLoadFooter;
import com.hzw.srecyclerview.AbsRefreshHeader;
import com.hzw.srecyclerview.SRecyclerViewModule;

/**
 * 功能：
 * Created by 何志伟 on 2017/7/17.
 */

public class TestSRVModule implements SRecyclerViewModule {
    @Override
    public AbsRefreshHeader getRefreshHeader(Context context) {
        return new TestRefreshHeader(context);
    }

    @Override
    public AbsLoadFooter getLoadingFooter(Context context) {
        return null;
    }

    @Override
    public int getRefreshHeight(Context context) {
        return dip2px(60);
    }

    @Override
    public int getRefreshGravity() {
        return SRecyclerViewModule.REFRESH_BOTTOM;
        //return SRecyclerViewModule.REFRESH_CENTER;
    }

    @Override
    public int getRefreshDuration() {
        return 0;
    }


    private static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
