package com.hzw.srecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * author: hzw
 * time: 2018/2/5 下午5:24
 * description:
 */
public abstract class AbsStateView extends FrameLayout {

    public AbsStateView(@NonNull Context context) {
        this(context, null);
    }

    public AbsStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init();
    }

    /**
     * 状态View的异常时的刷新重新
     *
     * @param isAnim 是否有刷新动画
     */
    final public void retry(boolean isAnim) {
        if (listener != null) {
            listener.retry(isAnim);
        }
    }

    interface RetryListener {
        void retry(boolean isAnim);
    }

    private RetryListener listener;

    final void setRetryListener(RetryListener listener) {
        this.listener = listener;
    }

    public abstract void init();

}
