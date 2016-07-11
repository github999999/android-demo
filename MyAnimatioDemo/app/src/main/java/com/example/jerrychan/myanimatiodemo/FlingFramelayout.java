package com.example.jerrychan.myanimatiodemo;

import android.animation.ValueAnimator;
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
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by jerrychan on 16/7/5.
 * 自定义控件
 */
public class FlingFramelayout extends FrameLayout {

    private Paint mPaint;
    private Context mContext;
    private final static int DEFAULTE_COLOR_BG = Color.parseColor("#E9494C");
    private final static int DEFAULT_COLOR_TEXT = Color.BLACK;
    PointF mLeftPoint, mCurrentPoiont, mRightPoint, mMidllerPoint;
    private Path mLeftPath, mRightPath, mInitPath;
    private boolean mTouch;
    private final static float DEFAULT_RADIUS = 40;
    private TextView mTextView;
    private int mBgColor, mTextColor;
    private RadiusBean mRadiusBean;
    private boolean isStartAnim = false;


    public FlingFramelayout(Context context) {
        this(context, null);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBgColor);
        mCurrentPoiont = new PointF();
        mMidllerPoint = new PointF();
        mLeftPath = new Path();
        mRightPath = new Path();
        mInitPath = new Path();
        buildUpTheTextView();
        mRadiusBean = new RadiusBean(DEFAULT_RADIUS, DEFAULT_RADIUS);


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

    public void setBackGroundColor(int color) {
        mBgColor = color;
    }

    public FlingFramelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.FlingFramelayout);
        mBgColor = typedArray.getColor(R.styleable.FlingFramelayout_bgColor, DEFAULTE_COLOR_BG);
        mTextColor = typedArray.getColor(R.styleable.FlingFramelayout_textColor, DEFAULT_COLOR_TEXT);
        typedArray.recycle();
        init();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), mPaint, Canvas.ALL_SAVE_FLAG);

        mLeftPoint = new PointF(mRadiusBean.getLeftRadius(), getHeight() / 2);
        mRightPoint = new PointF(getMeasuredWidth() - mRadiusBean.getRightRadius(), getMeasuredHeight() / 2);
        //初始化中间点
        mMidllerPoint.x = (mLeftPoint.x + mRightPoint.x) / 2;
        mMidllerPoint.y = mLeftPoint.y;
        canvas.drawCircle(mLeftPoint.x, mLeftPoint.y, mRadiusBean.getLeftRadius(), mPaint);
        canvas.drawCircle(mRightPoint.x, mRightPoint.y, mRadiusBean.getRightRadius(), mPaint);


        if (mTouch) {
            drawTheView(canvas);
        } else {
            //设置回到圆圈点
            mTextView.setX(getWidth() / 2);
            mTextView.setY(getHeight() / 2);
            if (isStartAnim) {
                drawTheView(canvas);
            } else {
                initPath(canvas);
            }
        }
        canvas.restore();
        super.dispatchDraw(canvas);


    }

    private void drawTheView(Canvas canvas) {
        //绘制左边的贝塞尔曲线
        caculatePath(mLeftPath, mLeftPoint, canvas, mRadiusBean, false);
        //绘制右边的贝塞尔曲线
        caculatePath(mRightPath, mRightPoint, canvas, mRadiusBean, true);

        canvas.drawCircle(mCurrentPoiont.x, mCurrentPoiont.y, DEFAULT_RADIUS, mPaint);

        //将textview的中心放在当前手指位置
        mTextView.setX(mCurrentPoiont.x - mTextView.getWidth() / 2);
        mTextView.setY(mCurrentPoiont.y - mTextView.getHeight() / 2);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        mCurrentPoiont.set(event.getX(), event.getY());
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
                mTouch = false;
                startBounceAnim(mCurrentPoiont.x, mCurrentPoiont.y);
                if (isStartAnim) {
                    return false;
                }

        }

        postInvalidate();
        return true;
    }

    private void initPath(Canvas canvas) {
        float x = mRightPoint.x;
        float y = mRightPoint.y;
        float startX = mLeftPoint.x;
        float startY = mLeftPoint.y;
        // 根据角度算出四边形的四个点
        float dx = x - startX;
        float dy = y - startY;
        double a = Math.atan(dy / dx);
        float offsetX = (float) (mRadiusBean.getRightRadius() * Math.sin(a));
        float offsetY = (float) (mRadiusBean.getRightRadius() * Math.cos(a));
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

        mInitPath.reset();
        mInitPath.moveTo(x1, y1);
        mInitPath.quadTo(anchorX, anchorY, x2, y2);
        mInitPath.lineTo(x3, y3);
        mInitPath.quadTo(anchorX, anchorY, x4, y4);
        mInitPath.lineTo(x1, y1);

        canvas.drawPath(mInitPath, mPaint);


    }

    //贝塞尔曲线绘制
    private void caculatePath(Path tempPath, PointF tempPoint, Canvas canvas, RadiusBean radiusBean, boolean isRight) {
        float x;
        float y = mCurrentPoiont.y;
        if (isRight) {
            x = mCurrentPoiont.x + 50;

        } else {
            x = mCurrentPoiont.x - 50;
        }
        float startX = tempPoint.x;
        float startY = tempPoint.y;

        // 根据角度算出四边形的四个点
        float dx = x - startX;
        float dy = y - startY;
        double a = Math.atan(dy / dx);
        float offsetX, offsetY;
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));

        if (isRight) {
            offsetX = (float) (radiusBean.getRightRadius() * Math.sin(a));
            offsetY = (float) (radiusBean.getRightRadius() * Math.cos(a));
            radiusBean.setRightRadius(DEFAULT_RADIUS - distance / 30);
//            Log.e("setRIGHTRadius", radiusBean.getRightRadius() + "");
        } else {
            offsetX = (float) (radiusBean.getLeftRadius() * Math.sin(a));
            offsetY = (float) (radiusBean.getLeftRadius() * Math.cos(a));
            radiusBean.setLeftRadius(DEFAULT_RADIUS - distance / 30);
//            Log.e("setLeftRadius", radiusBean.getLeftRadius() + "");
        }
        if (radiusBean.getLeftRadius() < 20) {
            radiusBean.setLeftRadius(20);
        }
        if (radiusBean.getRightRadius() < 20) {
            radiusBean.setRightRadius(20);
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

        tempPath.reset();
        tempPath.moveTo(x1, y1);
        tempPath.quadTo(anchorX, anchorY, x2, y2);
        tempPath.lineTo(x3, y3);
        tempPath.quadTo(anchorX, anchorY, x4, y4);
        tempPath.lineTo(x1, y1);

        canvas.drawPath(tempPath, mPaint);
    }


    private void startBounceAnim(final float currentX, final float currentY) {
        ValueAnimator mAnimator;
        isStartAnim = true;
        mAnimator = ValueAnimator.ofFloat(100, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCurrentPoiont.x = ((value - 100) / 100) * (currentX - mMidllerPoint.x) + currentX;
                mCurrentPoiont.y = ((value - 100) / 100) * (currentY - mMidllerPoint.y) + currentY;
                if (value == 0) {
                    isStartAnim = false;
                }
                postInvalidate();
            }
        });
        mAnimator.setDuration(500).setInterpolator(new BounceInterpolator());

        if (mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mAnimator.start();
    }

}
