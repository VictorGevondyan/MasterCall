package com.flycode.paradoxidealmaster.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by acerkinght on 8/8/16.
 */
public class CircleView extends View {
    private Paint paint;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int dpSize =  1;
        DisplayMetrics dm = getResources().getDisplayMetrics() ;
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);

        paint = new Paint();
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 10, paint);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(Color.TRANSPARENT);

        setColor(color);
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setIsOutlineOnly(boolean isOutlineOnly) {
        if (isOutlineOnly) {
            paint.setStyle(Paint.Style.STROKE);
        } else {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
    }
}
