package com.hzw.srecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 功能：SRecyclerView默认的加载尾部
 * Created by 何志伟 on 2017/7/17.
 */
class SRVLoadFooter extends AbsLoadFooter {

    private View load, noMore;

    public SRVLoadFooter(Context context) {
        super(context);
    }

    public SRVLoadFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SRVLoadFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.srv_load_footer, this, false);
        noMore = view.findViewById(R.id.tv_src_loadNoMore);
        load = view.findViewById(R.id.v_srv_loading);
        addView(view);
    }

    @Override
    public void loadBegin() {
        load.setVisibility(VISIBLE);
        noMore.setVisibility(GONE);
    }

    @Override
    public void loadEnd() {
        load.setVisibility(GONE);
        noMore.setVisibility(GONE);
    }

    @Override
    public void loadingNoMoreData() {
        load.setVisibility(GONE);
        noMore.setVisibility(VISIBLE);
    }


}
