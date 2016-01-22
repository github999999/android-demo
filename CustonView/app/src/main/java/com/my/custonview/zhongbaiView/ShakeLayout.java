package com.my.custonview.zhongbaiView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.my.custonview.R;

/**
 * Created by linchen on 16/1/21.
 */
public class ShakeLayout extends LinearLayout {
    private CircleView one, two, three, four, five;
    private Context context;
    private int width, height;


    private static final int DEGREE = 30;

    private static final int DURATION=400;

    public ShakeLayout(Context context) {
        this(context, null);
    }

    public ShakeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_shake, this, true);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        one = (CircleView) findViewById(R.id.cirlce_one);
        two = (CircleView) findViewById(R.id.cirlce_two);
        three = (CircleView) findViewById(R.id.cirlce_three);
        four = (CircleView) findViewById(R.id.cirlce_four);
        five = (CircleView) findViewById(R.id.cirlce_five);
        initAnim();

    }

    private RotateAnimation leftRotate;
    private RotateAnimation rightRotate;
    private TranslateAnimation leftShake;
    private TranslateAnimation rightShake;



    private void initAnim() {
        //旋转动画
        leftRotate = new RotateAnimation(0, DEGREE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -2f);
        //重复的次数
        leftRotate.setRepeatCount(1);
        //设置旋转的模式，当前为反转模式
        leftRotate.setRepeatMode(Animation.REVERSE);
        leftRotate.setDuration(DURATION);
        //插值器
        leftRotate.setInterpolator(new LinearInterpolator());
        //监听事件
        leftRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startLefeShake();
                startRightRotate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rightRotate = new RotateAnimation(0, -DEGREE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -2f);
        rightRotate.setRepeatCount(1);
        rightRotate.setRepeatMode(Animation.REVERSE);
        rightRotate.setDuration(DURATION);
        rightRotate.setInterpolator(new LinearInterpolator());
        rightRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startRightShake();
                startLeftRotate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        leftShake=new TranslateAnimation(0, 2, 0, 0);
        leftShake.setDuration(DURATION);
        leftShake.setInterpolator(new CycleInterpolator(2));


        rightShake=new TranslateAnimation(0, -2, 0, 0);
        rightShake.setDuration(DURATION);
        rightShake.setInterpolator(new CycleInterpolator(2));






        one.startAnimation(leftRotate);
    }

    private void startLefeShake(){

        two.startAnimation(leftShake);
        three.startAnimation(leftShake);
        four.startAnimation(leftShake);
    }

    private void startRightShake(){

        two.startAnimation(rightShake);
        three.startAnimation(rightShake);
        four.startAnimation(rightShake);
    }


    private void startLeftRotate(){
        one.startAnimation(leftRotate);
    }

    private void startRightRotate(){
        five.startAnimation(rightRotate);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

    }


}
