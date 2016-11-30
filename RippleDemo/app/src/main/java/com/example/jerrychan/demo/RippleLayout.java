package com.example.jerrychan.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by jerrychan on 16/11/24.
 * 正中心水波纹效果
 */

public class RippleLayout extends RelativeLayout {
    @IntDef({ TYPE_BLACK, TYPE_WHITE })
    public @interface ColorType {

    }

    public enum RippleState {
        RIPPLE,
        NO_RIPPLE
    }

    private Paint mCirlePaint;
    private OnRippleDoneListener mOnRippleDoneListener;
    public final static int DEFAULT_BLACK_COLOR = 0xee3322;
    public final static int DEFAULT_WHITE_COLOR = 0xee3322;
    // 刷新时间，默认20毫秒
    private final static int DEFAULT_REFRESH_TIME = 20;

    // 默认500毫秒时间
    private final static int DEFAULT_TIME = 500;
    private int mBlackColor, mWhiteColor;
    private int mWidth, mHeight;
    // 半径
    private int mRadius = 0;
    private int mPlayTime;
    private int mCurrentRadius = 0;
    private boolean isClick = false;
    public static final int TYPE_BLACK = 0X00000001;
    public static final int TYPE_WHITE = 0X00000002;
    // 决定水波纹的颜色type,默认为黑色
    private int mColorType = TYPE_BLACK;
    private int mIncreaseValue = 0;
    private RippleState mState;
    private int mCurrentX, mCurrentY;
    private final static String TAG = RippleLayout.class.getSimpleName();

    public RippleLayout(Context context) {
        this(context, null);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RippleLayout,
                defStyleAttr, 0);
        mBlackColor = a.getColor(R.styleable.RippleLayout_ripple_black_color, DEFAULT_BLACK_COLOR);
        mWhiteColor = a.getColor(R.styleable.RippleLayout_ripple_white_color, DEFAULT_WHITE_COLOR);
        mPlayTime = a.getInt(R.styleable.RippleLayout_ripple_time, DEFAULT_TIME);
        a.recycle();
        init();
    }

    private void init() {
        mCirlePaint = new Paint();
        mCirlePaint.setAntiAlias(true);
        mCirlePaint.setStyle(Paint.Style.FILL);
        mCirlePaint.setColor(mBlackColor);
        mState = RippleState.NO_RIPPLE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        double halfWidth = mWidth / 2;
        double halfHeight = mHeight / 2;
        mRadius = (int) Math.sqrt(halfHeight * halfHeight + halfWidth * halfWidth);
        mIncreaseValue = mRadius / (mPlayTime / DEFAULT_REFRESH_TIME);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isClick) {
            drawRipple(canvas);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mColorType == TYPE_BLACK) {
            mCirlePaint.setColor(mBlackColor);
        } else {
            mCirlePaint.setColor(mWhiteColor);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void drawRipple(final Canvas canvas) {
        canvas.clipRect(0, 0, mWidth, mHeight);
        if (mCurrentRadius <= mRadius) {
            mState = RippleState.RIPPLE;
            Log.e("RippleState", mCurrentRadius + "");
            mCurrentRadius += mIncreaseValue;
            postInvalidate();
        } else {
            isClick = false;
            canvas.drawCircle(mWidth / 2, mHeight / 2, mCurrentRadius, mCirlePaint);
            mState = RippleState.NO_RIPPLE;
            if (mOnRippleDoneListener != null) {
                mOnRippleDoneListener.onDone();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isClick = false;
            break;
        case MotionEvent.ACTION_UP:
            refresh();
            break;
        }
        return super.onTouchEvent(event);

    }

    public void refresh() {
        mState = RippleState.RIPPLE;
        reset();
        isClick = true;
        invalidate();
    }

    private void reset() {
        isClick = false;
        setBackgroundColor(mWhiteColor);
        mCurrentRadius = 0;
    }

    public void setColorType(@ColorType int type) {
        mColorType = type;
    }

    public int getColorType() {
        return mColorType;
    }

    public interface OnRippleDoneListener {
        void onDone();
    }

    public void setOnRippleDoneListener(OnRippleDoneListener listener) {
        mOnRippleDoneListener = listener;
    }

    public RippleState getState() {
        return mState;
    }

    public void setState(RippleState state) {
        mState = state;
    }

}
