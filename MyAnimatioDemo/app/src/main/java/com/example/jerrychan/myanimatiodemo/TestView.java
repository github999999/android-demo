package com.example.jerrychan.myanimatiodemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jerrychan on 16/7/4.
 */
public class TestView extends TextView {
    private Paint paint;
    public TestView(Context context) {
        this(context,null);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShadowLayer(10, 15, 15, Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2,300,paint);

    }
}
