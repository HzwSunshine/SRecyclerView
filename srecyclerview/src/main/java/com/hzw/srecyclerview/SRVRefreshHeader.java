package com.hzw.srecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * 功能：SRecyclerView默认的刷新头部
 * Created by 何志伟 on 2017/7/17.
 */
class SRVRefreshHeader extends AbsRefreshHeader {

    private RotateAnimation upAnim, downAnim;
    private boolean isUp = true;
    private View progress, icon;
    private TextView tips;

    public SRVRefreshHeader(Context context) {
        super(context);
    }

    public SRVRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SRVRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.srv_refresh_header, this, false);
        tips = view.findViewById(R.id.tv_src_refreshTips);
        progress = view.findViewById(R.id.pb_srv_refreshProgress);
        icon = view.findViewById(R.id.img_srv_refreshIcon);
        addView(view);
        upAnim = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnim.setDuration(200);
        upAnim.setFillAfter(true);
        downAnim.setDuration(200);
        downAnim.setFillAfter(true);
    }

    @Override
    public void refresh(int state, int height) {
        switch (state) {
            case NORMAL:
                progress.setVisibility(GONE);
                icon.setVisibility(VISIBLE);
                tips.setText(getContext().getString(R.string.refresh_normal));
                isUp = true;
                break;
            case REFRESH:
                progress.setVisibility(VISIBLE);
                icon.clearAnimation();
                icon.setVisibility(GONE);
                tips.setText(getContext().getString(R.string.refreshing));
                break;
            case PREPARE_NORMAL:
                tips.setText(getContext().getString(R.string.refresh_normal));
                if (!isUp) icon.startAnimation(upAnim);
                isUp = true;
                break;
            case PREPARE_REFRESH:
                tips.setText(getContext().getString(R.string.refresh_prepare));
                if (isUp) icon.startAnimation(downAnim);
                isUp = false;
                break;
        }
    }


}
