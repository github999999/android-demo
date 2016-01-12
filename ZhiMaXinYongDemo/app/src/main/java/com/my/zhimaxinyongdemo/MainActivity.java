package com.my.zhimaxinyongdemo;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private ShowView showView;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showView= (ShowView) findViewById(R.id.show);
        showView.setNumString(450+"");
        showView.setTextString("信用不错");
        relativeLayout= (RelativeLayout) findViewById(R.id.rl);
        //背景渐变动画实现
        ValueAnimator coloranim= ObjectAnimator.ofInt(relativeLayout, "backgroundColor", 0xFFFF8080, 0xFF8080FF);
        //要根据更新条计算时间计算，这里只是简单写的数字
        coloranim.setDuration(2000);
        coloranim.setEvaluator(new ArgbEvaluator());
        coloranim.start();


    }
    //550 100  //650 150 //450 50
}
