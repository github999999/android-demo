package com.my.ownpulltorefreshdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by linchen on 16/1/5.
 */
    public class ThisView extends LinearLayout {
    private ListView listView;
    private int mTouchSlop;
    //之前的x,y
    private int lastX, lastY;
    //当前的x,y
    private int currentX, currentY;

    private int interceptX, interceptY;
    private int oldInterceptX, oldInterceptY;

    public ThisView(Context context) {

        super(context);
        init(context);
    }

    public ThisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //滑动的最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i) instanceof ListView) {
                listView = (ListView) getChildAt(i);
            }
        }
        if (listView == null) {
            try {
                throw new Exception("please checked out the Linearayout surely has a Listvew!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept=false;
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oldInterceptX = (int) ev.getX();
            oldInterceptY = (int) ev.getY();
            intercept=false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            interceptX = (int) ev.getX();
            interceptY = (int) ev.getY();
            int distance = interceptY - oldInterceptY;
            //向下滑动,listview到头顶了,滑动最小距离超过mTouchSlop，则进行下拉刷新的实现
            if (interceptY >oldInterceptY && Math.abs(distance) > mTouchSlop && isOnTop()) {
                Log.e("canReshresh",true+"");
                intercept=true;
            }else
                intercept=false;
        } else if (action == MotionEvent.ACTION_UP) {
            oldInterceptX = (int) ev.getX();
            oldInterceptY = (int) ev.getY();
            intercept=false;
        }
        return intercept;
    }

    private boolean isOnTop() {
        View child_one = listView.getChildAt(0);
        //通过第一个child判断是否到达顶端,并且进行下拉操作
        if (child_one.getY() - listView.getPaddingTop() == 0) {
            Log.e("isOnTop===", true + "");
            return  true;
        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        lastX = oldInterceptX;
        lastY = oldInterceptY;
        Log.e("lastY",lastY+"");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //由于onInterceptTouchEvent在ACTION_DOWN的时候返回的是false，所以事件往下发放，这里
                //如果写：
//                    lastX = (int) event.getX();
//                    lastY = (int) event.getY();
                //是拦截不到的
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                int moveX = Math.abs(currentX - lastX);
                int moveY = Math.abs(currentY - lastY);
                Log.e("currentY",currentY+"");
//                Log.e("lastY",lastY+"");

                if (moveY > moveX) {
                    scrollTo(0, -moveY);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                currentX = (int) event.getX();
                currentY = (int) event.getY();

                break;
        }
        return true;
    }


}
