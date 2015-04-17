package com.haibuzou.expandgrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/4/7.
 */
public class GridItemView extends TextView {

    private Paint rectPaint;
    private Paint triPaint;
    private boolean isDraw;

    public GridItemView(Context context) {
        super(context);
        init();
    }

    public GridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.BLACK);

        triPaint = new Paint();
        triPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,0,getWidth(),getHeight(),rectPaint);
        float len = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,7,getResources().getDisplayMetrics());
        if(isDraw){
            Path path = new Path();
            path.moveTo(getWidth()/3,getHeight()-len);
            path.lineTo(getWidth()/3-len,getHeight());
            path.lineTo(getWidth()/3+len,getHeight());
            canvas.drawPath(path,triPaint);
        }
        super.onDraw(canvas);
    }

    public void drawtriangle(boolean isDraw){
        this.isDraw = isDraw;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
