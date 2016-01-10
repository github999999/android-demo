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
import android.widget.Scroller;
import android.widget.TextView;

import com.my.ownpulltorefreshdemo.utils.DensityUtil;

/**
 * Created by linchen on 16/1/5.
 * github:https://github.com/JerryChan123/android-learning
 * csdn-blog:http://blog.csdn.net/Anny_Lin/article/details/50491501
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

    private Scroller mScroller;
    //下拉最大的距离
    private final  static int MAX_DISTANCE=100;
    private int maxSmoothDistance;
    //阻尼系数
    private float dampingFactor=1.0f;
    private boolean isRefresh=false;
    private  TextView refreshTextView;

    //下拉刷新头部的高度
    private int putRefreshHeight;

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
        mScroller=new Scroller(context);
        maxSmoothDistance= DensityUtil.dip2px(context,MAX_DISTANCE);
        Log.e("maxSmoothDistance==",maxSmoothDistance+"");
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i) instanceof ListView) {
                listView = (ListView) getChildAt(i);
            }
            if (getChildAt(i)instanceof TextView)
            {
                 refreshTextView= (TextView) getChildAt(i);
                putRefreshHeight=refreshTextView.getHeight();
                Log.e("putRefreshHeight","==="+putRefreshHeight);
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
            if (!mScroller.isFinished()){
                mScroller.abortAnimation();
                intercept=true;
            }

        } else if (action == MotionEvent.ACTION_MOVE) {
            interceptX = (int) ev.getX();
            interceptY = (int) ev.getY();
            int distance = interceptY - oldInterceptY;
            //向下滑动,listview到头顶了,滑动最小距离超过mTouchSlop，
            // 且当前正在刷新，则进行下拉刷新的实现
            if (interceptY >oldInterceptY
                    && Math.abs(distance) > mTouchSlop
                    && isOnTop()
                    &&!isRefresh) {
//                Log.e("canReshresh",true+"");
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
//            Log.e("isOnTop===", true + "");
            return  true;
        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        lastX = oldInterceptX;
        lastY = oldInterceptY;
//        Log.e("lastY",lastY+"");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //由于onInterceptTouchEvent在ACTION_DOWN的时候返回的是false，所以事件往下发放，这里
                //如果写：
//                    lastX = (int) event.getX();
//                    lastY = (int) event.getY();
                //是拦截不到的
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                int moveX = Math.abs(currentX - lastX);
                int moveY = Math.abs(currentY - lastY);
//                Log.e("currentY",currentY+"");
//                Log.e("lastY",lastY+"");

//                &&Math.abs(getScrollY())<maxSmoothDistance

                if (moveY > moveX) {
                    scrollTo(0, -(currentY-lastY));
                }
                break;
            case MotionEvent.ACTION_UP:

                lastX = (int) event.getX();
                lastY = (int) event.getY();
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                smoothScrollTo();
                break;
        }
        return true;
    }

    //移动要移动到得位置坐标destX,destY
    public void smoothScrollTo(){
        int scrollY=getScrollY();
        Log.e("scrolly-----------",""+getScrollY());
        if (Math.abs(scrollY)>0){
            //当下拉刷新布局全部显示时候，进行刷新操作，否则，不进行刷新操作
            if (Math.abs(scrollY)>putRefreshHeight){
                mScroller.startScroll(0,scrollY,0,-scrollY-putRefreshHeight,800);
                isRefresh=true;
                invalidate();
            }else
            {
                mScroller.startScroll(0,scrollY,0,-scrollY,800);
                invalidate();
                isRefresh=false;
            }


        }

    }
    @Override
    public void computeScroll() {
        //源码可知computeScrollOffset可用来判断Scroller滑动是否结束
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }else
        {
            if (isRefresh){
                refreshTextView.setText("正在刷新");
                listener.onRefresh();
            }
        }
    }

    public void onRefreshCompleted(){
        int scrollY=getScrollY();
        mScroller.startScroll(0,scrollY,0,-scrollY,800);
        invalidate();
        isRefresh=false;
        refreshTextView.setText("下拉刷新");
    }
    public onRefreshListener listener;
    public void setOnRefreshListener(onRefreshListener listener)
    {
        this.listener=listener;
    }
    public interface  onRefreshListener{
        //进行刷新
        void onRefresh();

    }


}
