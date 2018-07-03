package com.hzw.srecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 功能：抽象的加载尾部，可继承并自定义加载尾部
 * Created by 何志伟 on 2017/7/10.
 */
public abstract class AbsLoadFooter extends LinearLayout {

    /**
     * 加载完成，即加载成功
     */
    protected final static int LOAD_SUCCESS = 0;
    /**
     * 加载开始
     */
    protected final static int LOAD_BEGIN = 1;
    /**
     * 加载无更多数据
     */
    protected final static int LOAD_NO_MORE = 2;
    /**
     * 加载失败或错误
     */
    protected final static int LOAD_ERROR = 3;

    public AbsLoadFooter(Context context) {
        super(context);
    }

    public AbsLoadFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    final void initFooter() {
        LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setVisibility(GONE);
        setGravity(Gravity.CENTER);
        init();
    }

    final void loadBegin() {
        if (getVisibility() == GONE){
            setVisibility(VISIBLE);
        }
        loadingState(LOAD_BEGIN);
    }

    final void loadSuccess() {
        loadingState(LOAD_SUCCESS);
    }

    final void loadingNoMoreData() {
        loadingState(LOAD_NO_MORE);
    }

    final void loadingError() {
        loadingState(LOAD_ERROR);
    }

    /**
     * 刷新结束后如果有需要，可重写此方法重置LoadFooter
     */
    public void reset() {
        setVisibility(GONE);
    }

    /**
     * SRecyclerView的onDetachedFromWindow被调用，可能SRecyclerView所在的界面要被销毁，
     * 如果子类中有动画等未完成，可以重写此方法取消动画等耗时操作，避免造成内存泄露
     */
    public void srvDetachedFromWindow() {
    }

    /**
     * 加载尾部初始化
     */
    public abstract void init();

    /**
     * 加载更多的加载状态
     *
     * @param state 状态
     */
    public abstract void loadingState(int state);

}
