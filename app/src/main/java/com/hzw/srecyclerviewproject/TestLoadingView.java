package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.hzw.srecyclerview.AbsStateView;

/**
 * author: hzw
 * time: 2018/2/5 下午5:25
 * description:
 */

public class TestLoadingView extends AbsStateView {
    public TestLoadingView(@NonNull Context context) {
        super(context);
    }

    public TestLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void init() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.error_loading, this, true);
    }

}
