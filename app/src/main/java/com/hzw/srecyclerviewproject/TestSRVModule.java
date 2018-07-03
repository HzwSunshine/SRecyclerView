package com.hzw.srecyclerviewproject;

import android.content.Context;

import com.hzw.srecyclerview.AbsEmptyView;
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

    /**
     * 也可以只配置其中一项，使用默认的加载UI
     */
    @Override
    public AbsLoadFooter getLoadingFooter(Context context) {
        return new TestLoadFooter(context);
    }

    @Override public AbsEmptyView getEmptyView(Context context) {
        return new TestEmptyView(context);
    }

}
