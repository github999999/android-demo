package com.example.jerrychan.myanimatiodemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by jerrychan on 16/7/5.
 */
public class FlingFramelayout extends FrameLayout {

    private Paint mPaint;
    private Context mContext;
    private final static int DEFAULTE_COLOR_BG = Color.parseColor("#E9494C");
    private final static int DEFAULT_COLOR_TEXT=Color.WHITE;
    PointF mStartPoint, mCurrentPoiont;
    private Path mPath;
    private boolean mTouch;
    private float mRadius = 60;
    private final static float DEFAULT_RADIUS = 60;
    private TextView mTextView;
    private int mBgColor,mTextColor;

    public FlingFramelayout(Context context) {
        this(context, null);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBgColor);
        mStartPoint = new PointF(100, 100);
        mCurrentPoiont = new PointF();
        mPath = new Path();
        buildUpTheTextView();


    }

    private void buildUpTheTextView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextView = new TextView(mContext);
        mTextView.setLayoutParams(layoutParams);
        mTextView.setPadding(10, 10, 10, 10);
        mTextView.setTextColor(mTextColor);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        mTextView.setText("100");
        this.addView(mTextView);
    }

    public void setText(CharSequence numberStr) {
        mTextView.setText(numberStr);
    }

    public  void setBackGroundColor(int color){
        mBgColor=color;
    }

    public FlingFramelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray=mContext.obtainStyledAttributes(attrs,R.styleable.FlingFramelayout);
        mBgColor=typedArray.getColor(R.styleable.FlingFramelayout_bgColor,DEFAULTE_COLOR_BG);
        mTextColor=typedArray.getColor(R.styleable.FlingFramelayout_textColor,DEFAULT_COLOR_TEXT);
        typedArray.recycle();
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(mStartPoint.x, mStartPoint.y, mRadius, mPaint);
        if (mTouch) {
            caculatePath();
            canvas.drawCircle(mStartPoint.x, mStartPoint.y, mRadius, mPaint);
            canvas.drawCircle(mCurrentPoiont.x, mCurrentPoiont.y, mRadius, mPaint);
            canvas.drawPath(mPath, mPaint);
            //将textview的中心放在当前手指位置
            mTextView.setX(mCurrentPoiont.x - mTextView.getWidth() / 2);
            mTextView.setY(mCurrentPoiont.y - mTextView.getHeight() / 2);
        } else {
            //设置回到圆圈点
            mTextView.setX(mStartPoint.x - mTextView.getWidth() / 2);
            mTextView.setY(mStartPoint.y - mTextView.getHeight() / 2);
        }
        canvas.restore();
        super.dispatchDraw(canvas);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 判断触摸点是否在tipImageView中
                Rect rect = new Rect();
                int[] location = new int[2];
                mTextView.getLocationOnScreen(location);
                rect.left = location[0];
                rect.top = location[1];
                rect.right = mTextView.getWidth() + location[0];
                rect.bottom = mTextView.getHeight() + location[1];
                if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mTouch = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                mRadius = DEFAULT_RADIUS;
                mTouch = false;

        }
        mCurrentPoiont.set(event.getX(), event.getY());
        postInvalidate();
        return true;
    }

    //使用贝塞尔曲线进行两圆圈之间的区域的绘制
    private void caculatePath() {
        float x = mCurrentPoiont.x;
        float y = mCurrentPoiont.y;
        float startX = mStartPoint.x;
        float startY = mStartPoint.y;

        // 根据角度算出四边形的四个点
        float dx = x - startX;
        float dy = y - startY;
        double a = Math.atan(dy / dx);
        float offsetX = (float) (mRadius * Math.sin(a));
        float offsetY = (float) (mRadius * Math.cos(a));

        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        mRadius = DEFAULT_RADIUS - distance / 15;

        if (mRadius < 20) {
            mRadius = 20;
        }

        // 根据角度算出四边形的四个点
        float x1 = startX + offsetX;
        float y1 = startY - offsetY;

        float x2 = x + offsetX;
        float y2 = y - offsetY;

        float x3 = x - offsetX;
        float y3 = y + offsetY;

        float x4 = startX - offsetX;
        float y4 = startY + offsetY;

        float anchorX = (startX + x) / 2;
        float anchorY = (startY + y) / 2;

        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.quadTo(anchorX, anchorY, x2, y2);
        mPath.lineTo(x3, y3);
        mPath.quadTo(anchorX, anchorY, x4, y4);
        mPath.lineTo(x1, y1);
    }

}
