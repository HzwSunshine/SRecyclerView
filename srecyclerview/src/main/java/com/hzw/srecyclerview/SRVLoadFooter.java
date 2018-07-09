package com.hzw.srecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 功能：SRecyclerView默认的加载尾部
 * Created by 何志伟 on 2017/7/17.
 */
class SRVLoadFooter extends AbsLoadFooter {

    private View load, noMore, error;

    public SRVLoadFooter(Context context) {
        super(context);
    }

    public SRVLoadFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SRVLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void init() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.srv_load_footer, this, false);
        noMore = view.findViewById(R.id.tv_src_loadNoMore);
        load = view.findViewById(R.id.v_srv_loading);
        error = view.findViewById(R.id.tv_src_loadError);
        error.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                errorRetry();
            }
        });
        addView(view);
    }

    @Override public void loadingState(int state) {
        switch (state) {
            case LOAD_SUCCESS:
                load.setVisibility(GONE);
                noMore.setVisibility(GONE);
                error.setVisibility(GONE);
                break;
            case LOAD_ERROR:
                load.setVisibility(GONE);
                noMore.setVisibility(GONE);
                error.setVisibility(VISIBLE);
                break;
            case LOAD_NO_MORE:
                load.setVisibility(GONE);
                noMore.setVisibility(VISIBLE);
                error.setVisibility(GONE);
                break;
            case LOAD_BEGIN:
                load.setVisibility(VISIBLE);
                noMore.setVisibility(GONE);
                error.setVisibility(GONE);
                break;
        }
    }

}
