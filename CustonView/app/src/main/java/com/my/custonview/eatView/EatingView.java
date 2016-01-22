package com.my.custonview.eatView;

import android.animation.FloatArrayEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.my.custonview.DensityUtil;

/**
 * Created by linchen on 16/1/22.
 */
public class EatingView extends View {

    private Context context;
    private int width, height;
    private Paint whiteCirlcePaint, eyePaint, mouthPaint;


    private static final float START_ANGLE = -20;
    private static final float SWEEP_ANGLE = 40;

    private static final float HALF_ANGLE = SWEEP_ANGLE / 2;

    private boolean isShutUp = true;

    private float rate_angle = 0;

    private float ballMoveX = 0;

    private ObjectAnimator objectAnimator;

    public EatingView(Context context) {
        this(context, null);
    }

    public EatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {

        whiteCirlcePaint = new Paint();
        whiteCirlcePaint.setStyle(Paint.Style.FILL);
        whiteCirlcePaint.setColor(Color.parseColor("#ffffff"));
        whiteCirlcePaint.setAntiAlias(true);


        eyePaint = new Paint();
        eyePaint.setStyle(Paint.Style.FILL);
        eyePaint.setColor(Color.parseColor("#ee2222"));
        eyePaint.setAntiAlias(true);

        mouthPaint = new Paint();
        mouthPaint.setStyle(Paint.Style.FILL);
        mouthPaint.setColor(Color.parseColor("#ee2222"));
        mouthPaint.setAntiAlias(true);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //白色圆形
        canvas.drawCircle(height / 2, height / 2, height / 2, whiteCirlcePaint);
        //眼睛
        canvas.drawCircle(height / 2, height / 2 / 2, height / 2 / 2 / 2, eyePaint);

        //嘴巴
        RectF rectF = new RectF(0, 0, height, height);
        canvas.drawArc(rectF, START_ANGLE + rate_angle, SWEEP_ANGLE - rate_angle * 2, true, mouthPaint);


        //三个小圆圈
        canvas.drawCircle(height - ballMoveX,
                height / 2,
                height / 2 / 2 / 2 / 2,
                whiteCirlcePaint);
        canvas.drawCircle(height + height / 2 - ballMoveX,
                height / 2,
                height / 2 / 2 / 2 / 2,
                whiteCirlcePaint);
        canvas.drawCircle(height + height - ballMoveX,
                height / 2,
                height / 2 / 2 / 2 / 2,
                whiteCirlcePaint);


    }


    private void startAnim() {
         objectAnimator = ObjectAnimator.ofFloat(new Object(), "", 2 * HALF_ANGLE);
        objectAnimator.setEvaluator(new FloatEvaluator());
        objectAnimator.setDuration(800);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //张嘴闭嘴动画
                if (rate_angle >= HALF_ANGLE) {
                    rate_angle = 2 * HALF_ANGLE - value;

                } else {
                    rate_angle = value;
                }
                //公式：height/2 *value=2*HALF_ANGLE*ballMoveX;
                ballMoveX = (height
                ) * value / (4 * HALF_ANGLE);

                postInvalidate();
            }
        });
        objectAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        Log.e("width and height:", "width=" + width + "-----height=" + height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wrap_Len = DensityUtil.dip2px(context, 200);
        int w = measureDimension(wrap_Len, widthMeasureSpec);
        int h = measureDimension(wrap_Len, heightMeasureSpec);
//        len=Math.min(width,height);
        //保证他是一个正方形
        setMeasuredDimension(w, h);

    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        objectAnimator.cancel();
    }
}
