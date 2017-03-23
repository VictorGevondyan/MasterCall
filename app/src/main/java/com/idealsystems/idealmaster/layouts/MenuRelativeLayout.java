package com.idealsystems.idealmaster.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by anhaytananun on 05.07.16.
 */
public class MenuRelativeLayout extends RelativeLayout {
    public MenuRelativeLayout(Context context) {
        super(context);
    }

    public MenuRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
