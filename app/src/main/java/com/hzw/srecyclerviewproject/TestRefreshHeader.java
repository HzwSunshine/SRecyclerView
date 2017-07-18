package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hzw.srecyclerview.AbsRefreshHeader;

/**
 * 功能：
 * Created by 何志伟 on 2017/7/17.
 */

public class TestRefreshHeader extends AbsRefreshHeader {

    private TextView refreshText;
    private ClockView clockView;

    public TestRefreshHeader(Context context) {
        super(context);
    }

    public TestRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(int refreshHeight) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.refresh_view, this, false);
        clockView = (ClockView) view.findViewById(R.id.v_refresh);
        refreshText = (TextView) view.findViewById(R.id.tv_refresh);
        addView(view);
    }

    @Override
    public void refresh(int state, int height) {
        switch (state) {
            case NORMAL:
                refreshText.setText("下拉刷新");
                clockView.stopClockAnim();
                break;
            case REFRESH:
                refreshText.setText("正在刷新...");
                clockView.startClockAnim();
                break;
            case PREPARE_NORMAL:
                refreshText.setText("下拉刷新");
                clockView.setClockAngle(height);
                break;
            case PREPARE_REFRESH:
                refreshText.setText("释放立即刷新");
                clockView.setClockAngle(height);
                break;
        }
    }

}
