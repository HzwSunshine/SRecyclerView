package com.hzw.srecyclerviewproject;

import android.content.Context;

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


}
