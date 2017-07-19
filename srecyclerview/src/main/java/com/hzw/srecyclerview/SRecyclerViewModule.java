package com.hzw.srecyclerview;

import android.content.Context;

/**
 * 功能：全局的SRecyclerView配置
 * Created by 何志伟 on 2017/7/17.
 */
public interface SRecyclerViewModule {

    AbsRefreshHeader getRefreshHeader(Context context);

    AbsLoadFooter getLoadingFooter(Context context);


}
