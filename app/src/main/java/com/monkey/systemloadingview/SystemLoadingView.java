package com.monkey.systemloadingview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * 自定义LoadingView，仿原生ProgressBar
 */
public class SystemLoadingView extends View {

    private long mAnimDuration = 1500;
    private boolean mIsLoading = false;

    private Paint mPaint;
    private Path mPath;
    private Path mDst;
    private PathMeasure mPathMeasure;
    private float mPathLength;
    private float mPathPercent;

    private int mColor;
    private float mRadius;
    private float mArcWidth = 24;

    public SystemLoadingView(Context context) {
        this(context, null);
    }

    public SystemLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SystemLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SystemLoadingView, defStyleAttr, 0);
        mColor = ta.getColor(R.styleable.SystemLoadingView_color, getResources().getColor(R.color.colorPrimary));
        mRadius = ta.getDimension(R.styleable.SystemLoadingView_radius, DensityUtils.dp2px(context, 32));
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mArcWidth);
        mPaint.setColor(mColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();
        mDst = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = (int) (2 * mRadius + mArcWidth);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mPath.addCircle(w / 2, h / 2, mRadius, Path.Direction.CW);
        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mPath, false);
        mPathLength = mPathMeasure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float stop = mPathLength * mPathPercent;
        float start = (float) (stop - ((0.5 - Math.abs(mPathPercent - 0.5)) * mPathLength * 4));
        mDst.reset();
//        mDst.lineTo(0, 0);
        mPathMeasure.getSegment(start, stop, mDst, true);
        canvas.drawPath(mDst, mPaint);
    }

    private void startAnim() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setDuration(mAnimDuration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPathPercent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();

        ObjectAnimator animRotate = ObjectAnimator.ofFloat(this, View.ROTATION, 0, 360);
        animRotate.setInterpolator(new LinearInterpolator());
        animRotate.setRepeatCount(ValueAnimator.INFINITE);
        animRotate.setDuration(2 * mAnimDuration);
        animRotate.start();
    }


    public void start() {
        mIsLoading = true;
        setVisibility(View.VISIBLE);
        startAnim();
    }

    public void stop() {
        mIsLoading = false;
        setVisibility(View.GONE);
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
