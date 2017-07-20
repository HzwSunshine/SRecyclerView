package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzw.srecyclerview.AbsLoadFooter;

/**
 * 功能：
 * Created by 何志伟 on 2017/7/19.
 */

public class TestLoadFooter extends AbsLoadFooter {

    private View load, noMore;

    public TestLoadFooter(Context context) {
        super(context);
    }

    public TestLoadFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestLoadFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        View loadView = LayoutInflater.from(getContext()).inflate(R.layout.srv_load_footer, this, false);
        noMore = loadView.findViewById(R.id.tv_src_loadNoMore);
        load = loadView.findViewById(R.id.v_srv_loading);
        addView(loadView);
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

    /**
     * 据说有一种需求是，没数据时，直接不显示无数据的UI，此时可设置高度为0，然后重写reset()方法，
     * 当列表刷新时会重置加载尾部
     */
    @Override
    public void loadingNoMoreData() {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = 0;
        setLayoutParams(params);
    }

    @Override
    public void reset() {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = dip2px(45);
        setLayoutParams(params);
    }

    private int dip2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }


}
