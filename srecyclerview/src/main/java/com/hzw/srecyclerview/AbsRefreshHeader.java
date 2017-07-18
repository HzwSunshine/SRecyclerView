package com.hzw.srecyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * 功能：抽象的刷新头部，自定义头部可继承并自定义
 * Created by 何志伟 on 2017/7/6.
 */

public abstract class AbsRefreshHeader extends LinearLayout {

    protected final static int NORMAL = 0;//正常状态，或者刷新结束的状态
    protected final static int REFRESH = 1;//正在刷新的状态
    protected final static int PREPARE_NORMAL = 2;//刷新前的状态，未超过刷新临界值
    protected final static int PREPARE_REFRESH = 3;//刷新前的状态，已超过刷新临界值
    private ValueAnimator animator;
    private int refreshHeight;
    private int currentHeight;
    private int currentState;
    private int duration;

    public AbsRefreshHeader(Context context) {
        super(context);
    }

    public AbsRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    final void initHeader(int height, int gravity, int duration) {
        this.duration = duration;
        refreshHeight = height;
        ViewGroup.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        setLayoutParams(params);
        if (gravity == SRecyclerViewModule.REFRESH_BOTTOM) {
            setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        } else if (gravity == SRecyclerViewModule.REFRESH_CENTER) {
            setGravity(Gravity.CENTER);
        }
        init(height);
    }

    /**
     * 手指拖动时
     */
    final void move(float delay) {
        if (currentState == REFRESH) return;
        delay = delay / 3;
        currentHeight += delay;
        setHeight(currentHeight);
        //拖动时的状态
        if (currentHeight == 0) {
            currentState = NORMAL;
            refresh(NORMAL, currentHeight);
        } else if (currentHeight < refreshHeight) {
            currentState = PREPARE_NORMAL;
            refresh(PREPARE_NORMAL, currentHeight);
        } else if (currentHeight >= refreshHeight) {
            currentState = PREPARE_REFRESH;
            refresh(PREPARE_REFRESH, currentHeight);
        }
    }

    /**
     * 手指抬起时
     */
    final void up() {
        //手指抬起时，如果当前处于刷新状态或者刷新头部高度为0，则
        if (currentState == REFRESH || currentState == NORMAL) return;
        //处于将要刷新状态时，松开手指即可刷新，同时改变到刷新高度
        if (currentState == PREPARE_REFRESH && loadListener != null) {
            refresh(REFRESH, refreshHeight);
            loadListener.refresh();
        }
        heightChangeAnim();
    }

    private void heightChangeAnim() {
        if (animator != null && animator.isRunning()) return;
        int start, end;
        //需要高度动画的状态有：PREPARE_REFRESH，PREPARE_NORMAL，REFRESH
        if (currentState == REFRESH) {
            //刷新结束变为正常
            start = currentHeight;
            end = 0;
        } else if (currentState == PREPARE_NORMAL) {
            //在低于刷新高度的位置松开拖动，准备回归初始状态
            start = currentHeight;
            end = 0;
        } else if (currentState == PREPARE_REFRESH) {
            //在高于刷新高度的位置松开拖动，准备开始刷新
            start = currentHeight;
            end = refreshHeight;
        } else if (currentState == NORMAL) {
            //代码调用开始刷新，准备开始刷新
            start = 0;
            end = refreshHeight;
        } else return;
        if (animator == null) {
            animator = ValueAnimator.ofInt(start, end);
            animator.setDuration(duration).setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int height = (int) animation.getAnimatedValue();
                    setHeight(height);
                    //高度自动更新的两个状态：刷新结束后的状态，未达到刷新高度而松手的状态
                    if (currentState == PREPARE_NORMAL || currentState == NORMAL) {
                        refresh(PREPARE_NORMAL, height);
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    heightChangeAnimEnd();
                }
            });
        } else {
            animator.setIntValues(start, end);
        }
        animator.start();
    }

    private void setHeight(int height) {
        height = height < 0 ? 0 : height;
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    final boolean isMove() {
        return getLayoutParams().height > 0 && currentState != REFRESH;
    }

    /**
     * 如果界面销毁时，取消动画，防止内存泄露
     * 并且调用子类的初始化状态，如果子类中有动画，则可以取消
     */
    final void cancelAnim() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            refresh(NORMAL, 0);
        }
    }

    /**
     * 高度动画结束时，currentState只可能有四种状态：
     * PREPARE_REFRESH，PREPARE_NORMAL，REFRESH和NORMAL
     */
    private void heightChangeAnimEnd() {
        switch (currentState) {
            case REFRESH://刷新结束
                currentState = NORMAL;
                currentHeight = 0;
                refresh(NORMAL, 0);
                break;
            case PREPARE_NORMAL://在低于刷新高度的位置松开拖动，当前已回归初始状态
                currentState = NORMAL;
                currentHeight = 0;
                break;
            case PREPARE_REFRESH://在高于刷新高度的位置松开拖动，当前已开始刷新
                currentState = REFRESH;
                currentHeight = refreshHeight;
                break;
            case NORMAL://代码调用自动刷新，当前已开始刷新
                currentState = REFRESH;
                currentHeight = refreshHeight;
                refresh(REFRESH, refreshHeight);
                loadListener.refresh();
                break;
        }
    }

    /**
     * 请求数据，刷新完成，恢复初始状态
     */
    final void refreshComplete() {
        if (animator != null && animator.isRunning()) {
            //在刷新和刷新完成之间调用过快
            animator.cancel();
        }
        currentState = REFRESH;
        heightChangeAnim();
    }

    final void startRefresh(final boolean isAnim) {
        if (loadListener != null && currentState != REFRESH) {
            post(new Runnable() {
                @Override
                public void run() {
                    currentState = NORMAL;
                    if (isAnim) {
                        heightChangeAnim();
                    } else {
                        heightChangeAnimEnd();
                    }
                }
            });
        }

    }

    interface ReFreshListener {
        void refresh();
    }

    private ReFreshListener loadListener;

    final void setRefreshListener(ReFreshListener listener) {
        loadListener = listener;
    }

    /**
     * 子类刷新头初始化
     *
     * @param refreshHeight 刷新临界值
     */
    public abstract void init(int refreshHeight);


    /**
     * 刷新头部的调用方法
     * <p>
     * NORMAL:初始化状态或者刷新结束后状态，刷新结束，高度变为0时，会调用此状态
     * <p>
     * REFRESH:正在刷新状态，高度从松开手指时的高度到刷新变为0的高度之间，都属于刷新状态，
     * --------高度变为0时，状态变为NORMAL，会调用NORMAL状态
     * <p>
     * PREPARE_NORMAL:准备回归初始化的状态，手指移动时当前高度小于刷新高度，此时松开手指，不会调用NORMAL状态，
     * ---------------或者自动刷新时当前高度未达到刷新高度的状态，达到刷新高度后会调用REFRESH状态
     * <p>
     * PREPARE_REFRESH:准备刷新的状态，手指移动时当前高度大于刷新高度，此时松开手指，会立刻调用REFRESH状态，
     * ---------------同时高度变为刷新高度
     *
     * @param state  分别为：NORMAL，REFRESH，PREPARE_NORMAL，PREPARE_REFRESH
     * @param height 当前刷新头的高度
     */
//    switch (state) {
//        case NORMAL:
//
//            break;
//        case REFRESH:
//
//            break;
//        case PREPARE_NORMAL:
//
//            break;
//        case PREPARE_REFRESH:
//
//            break;
//    }
    public abstract void refresh(int state, int height);


}
