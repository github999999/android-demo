package com.example.jerrychan.myanimatiodemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by jerrychan on 16/7/8.
 */
public class SlingShotLayout extends FrameLayout {
    //背景颜色的画笔
    private Paint mPaint;
    private Context mContext;
    private final static int DEFAULTE_COLOR_BG = Color.parseColor("#E9494C");
    private PointF mLeftPoint, mRightPoint;
    private int radius = 30;
    private Path mPath;
    private boolean isTouch = false;
    private PointF mCurTouchPoint;

    public SlingShotLayout(Context context) {
        this(context, null);
    }

    public SlingShotLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(DEFAULTE_COLOR_BG);
        mPath = new Path();
        mCurTouchPoint = new PointF();


    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mLeftPoint = new PointF(30, getMeasuredHeight() / 2);
        mRightPoint = new PointF(getMeasuredWidth() - 30, getMeasuredHeight() / 2);
        canvas.drawCircle(mLeftPoint.x, mLeftPoint.y, radius, mPaint);
        canvas.drawCircle(mRightPoint.x, mRightPoint.y, radius, mPaint);
        if (mCurTouchPoint.x!=0||mCurTouchPoint.y!=0){
            canvas.drawCircle(mCurTouchPoint.x,mCurTouchPoint.y,radius,mPaint);
        }
        super.dispatchDraw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouch = true;

                //获取当前触摸位置坐标
                mCurTouchPoint.x = event.getX();
                mCurTouchPoint.y = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;

        }
        postInvalidate();
        return true;
    }
}
