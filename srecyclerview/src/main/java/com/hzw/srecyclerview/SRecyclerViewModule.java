package com.hzw.srecyclerview;

import android.content.Context;

/**
 * 功能：全局的SRecyclerView配置
 * Created by 何志伟 on 2017/7/17.
 */
public interface SRecyclerViewModule {

    int REFRESH_BOTTOM = 1;
    int REFRESH_CENTER = 2;

    AbsRefreshHeader getRefreshHeader(Context context);

    AbsLoadFooter getLoadingFooter(Context context);

    int getRefreshHeight(Context context);

    int getRefreshGravity();

    int getRefreshDuration();


}
