package com.my.viewpagerindicator.dicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.viewpagerindicator.R;

import java.util.List;

/**
 * Created by linchen on 16/1/8.
 */
public class ViewPagerIndicator extends LinearLayout {
    //默认可见的tab等于3个
    private static  final int DEFAULT_COUNT=3;

    //可见tab的数量
    private int visible_count;
    private int all_count;
    //绘制图形的画笔
    private Paint mPaint;

    //三角形路径
    private Path mPath;

    //三角形高度
    private int trianHeigth;
    //三角形宽度
    private int trianWidth;

    //三角形的宽度为单个Tab的1/6
    private static final float RADIO_TRIANGEL = 1.0f / 6;

    // 三角形的最大宽度
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getWidth() / 3 * RADIO_TRIANGEL);

    //三角型初始的偏移量
    private   int mTrianTranslateX;

    /**
     * 手指滑动时的偏移量
     */
    private float mTranslationX;


    /**
     * tab上的内容
     */
    private List<String> mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;





    public ViewPagerIndicator(Context context) {
        this(context,null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义的属性
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        visible_count=typedArray.getInt(R.styleable.ViewPagerIndicator_visible_count,DEFAULT_COUNT);
        if (visible_count<0){
            visible_count=3;
        }
        //回收资源
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        //这里设置了setPathEffect，就是为了画的线的连接处，有点圆角~~
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count=getChildCount();
        if (count==0)
            return;
        for (int i=0;i<count;i++){

            View view=getChildAt(i);
            LinearLayout.LayoutParams layoutParams= (LayoutParams) view.getLayoutParams();
            layoutParams.weight=0;
            layoutParams.width=getWidth()/visible_count;
            view.setLayoutParams(layoutParams);
        }
        setItemClick();
    }

    //设置各个view的点击事件，这里跟viewpager关联起来
    public void setItemClick() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 初始化三角形的宽度
     */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        trianWidth= (int) (w/visible_count*RADIO_TRIANGEL);
        trianWidth=Math.min(trianHeigth,DIMENSION_TRIANGEL_WIDTH);
        initTriangle();

        // 初始时的偏移量
        mTrianTranslateX = getWidth() / visible_count / 2 - trianWidth
                / 2;
    }

    private void initTriangle() {
        mPath=new Path();
        trianHeigth=(int) (trianWidth / 2 / Math.sqrt(2));
        mPath.moveTo(0, 0);
        mPath.lineTo(trianWidth, 0);
        mPath.lineTo(trianWidth / 2, -trianHeigth);
        mPath.close();
    }

    //在绘制子View之前，我们先绘制我们的三角形指示器~~
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mTrianTranslateX-mTranslationX,getHeight()+1);
        canvas.drawPath(mPath,mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);

    }

    public void bindViewPager(ViewPager vp,int position){
        mViewPager=vp;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //positionOffset的区间为[0,1]，代表着滑动开始(0)到滑动结束(1)
                scroll(position,positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                //重新设置字体高亮
                resetTextViewColor();
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(position);
        // 高亮
        highLightTextView(position);
    }

    //指示器跟随手指滚动，以及容器滚动
    private void scroll(int position, float positionOffset) {
        mTranslationX=getWidth()/visible_count*(position+positionOffset);
        int tabWidth = getWidth() / visible_count;
        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (positionOffset > 0 && position >= (visible_count - 2)
                && getChildCount() > visible_count)
        {
            if (visible_count != 1)
            {
                this.scrollTo((position - (visible_count - 2)) * tabWidth
                        + (int) (tabWidth * positionOffset), 0);
            } else
            // 为count为1时 的特殊处理
            {
                this.scrollTo(
                        position * tabWidth + (int) (tabWidth * positionOffset), 0);
            }
        }

        invalidate();
    }

    private void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView)
        {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        }
    }

    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++)
        {
            View view = getChildAt(i);
            if (view instanceof TextView)
            {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }
    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count)
    {
        this.visible_count = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas)
    {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0)
        {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles)
            {
                // 添加view
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }

    }

}
