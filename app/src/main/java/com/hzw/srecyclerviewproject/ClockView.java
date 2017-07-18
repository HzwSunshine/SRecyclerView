package com.hzw.srecyclerviewproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 功能：时钟View
 * Created by hzw on 2016/12/15.
 */
public class ClockView extends View {

    private static final int GRAY_COLOR = Color.parseColor("#434659");
    private Paint paint = new Paint();
    private float sharpRadius;//整点时间的半径
    private float otherRadius;//非整点的半径
    private float outsideCircleWidth;//外圆宽度
    private float outsideDistance;//整点圆心到外圆的距离
    private float needleWidth;//指针的宽度
    private float hourLength;//时钟的长度
    private float minuteLength;//分针的长度
    private float centerCircleRadius;//中心圆的半径
    private int angle;//时针的角度
    private int size;//表盘的宽高
    private ValueAnimator animator;


    public ClockView(Context context) {
        super(context);
        init(null, 0);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, 0);
        int color = array.getColor(R.styleable.ClockView_colck_color, GRAY_COLOR);//时钟颜色
        array.recycle();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(getWidth(), getHeight());
        int factor = 524;//默认624
        sharpRadius = size * 30.0f / (factor * 2.0f);
        otherRadius = size * 18.0f / (factor * 2.0f);
        outsideCircleWidth = size * 25.0f / factor;
        outsideDistance = size * 95.0f / factor;
        hourLength = size * 180.0f / factor;
        minuteLength = size * 110.0f / factor;
        needleWidth = size * 35.0f / (factor * 2.0f);
        centerCircleRadius = needleWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画背景和外圆
        canvas.drawColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outsideCircleWidth);
        canvas.drawCircle(size / 2, size / 2, (size - outsideCircleWidth) / 2, paint);
        //画时间圆点
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0) {
                canvas.drawCircle(size / 2, outsideDistance, sharpRadius, paint);
            } else {
                canvas.drawCircle(size / 2, outsideDistance, otherRadius, paint);
            }
            canvas.rotate(30, size / 2, size / 2);
        }

        paint.setStrokeWidth(needleWidth);
        //画时针
        canvas.drawLine(size / 2, size / 2, getX(minuteLength, angle), getY(minuteLength, angle), paint);
        //画分针
        int hourAngle = (angle % 30) * 12 - 90;
        canvas.drawLine(size / 2, size / 2, getX(hourLength, hourAngle), getY(hourLength, hourAngle), paint);

        //画中心圆点
        canvas.drawCircle(size / 2, size / 2, centerCircleRadius, paint);
    }

    private float getX(float radius, int angle) {
        return (float) (size / 2 + radius * Math.cos(angle * 3.14 / 180));
    }

    private float getY(float radius, int angle) {
        return (float) (size / 2 + radius * Math.sin(angle * 3.14 / 180));
    }

    public void setClockAngle(int angle) {
        angle /= 4;
        this.angle = angle - 90;
        invalidate();
    }

    public void resetClock() {
        angle = -90;
    }

    public void startClockAnim() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(angle, angle + 360);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(10000).setInterpolator(new LinearInterpolator());
            animator.start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    angle = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
        } else {
            animator.setIntValues(angle, angle + 360);
            animator.start();
        }
    }

    public void stopClockAnim() {
        if (animator != null) {
            animator.cancel();
        }
    }


}
