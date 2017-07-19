package com.hzw.srecyclerviewproject;

import android.content.Context;
import android.content.res.Resources;
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
    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.refresh_view, this, false);
        clockView = (ClockView) view.findViewById(R.id.v_refresh);
        refreshText = (TextView) view.findViewById(R.id.tv_refresh);
        addView(view);
    }


    /**
     * 如果需要设置刷新动画时间，可以重写此方法
     */
    @Override
    public int getRefreshDuration() {
        return 300;
    }

    /**
     * 如果需要设置头部的Gravity，可以重写此方法
     *
     * @return HEADER_CENTER，HEADER_BOTTOM
     */
    @Override
    public int getRefreshGravity() {
        return AbsRefreshHeader.HEADER_CENTER;
//        return AbsRefreshHeader.HEADER_BOTTOM;
    }

    /**
     * 如果需要设置刷新高度，也就是刷新临界值，可以重写此方法
     */
    @Override
    public int getRefreshHeight() {
        return dip2px(70);
    }

    private int dip2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    /**
     * SRecyclerView的onDetachedFromWindow被调用，可能SRecyclerView所在的界面要被销毁，
     * 如果子类中有动画等未完成，可以重写此方法取消动画等耗时操作，避免造成内存泄露
     */
    @Override
    public void srvDetachedFromWindow() {
        if(clockView!=null){
            clockView.resetClock();
        }
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
