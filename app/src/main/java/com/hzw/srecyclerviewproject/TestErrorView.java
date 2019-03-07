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

public class TestErrorView extends AbsStateView {
    public TestErrorView(@NonNull Context context) {
        super(context);
    }

    public TestErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.error_test, this, true);

        //空布局的点击刷新
        view.findViewById(R.id.empty_click).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                retry(true);
            }
        });
    }

}
