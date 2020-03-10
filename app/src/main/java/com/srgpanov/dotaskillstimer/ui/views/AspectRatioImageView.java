package com.srgpanov.dotaskillstimer.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.srgpanov.dotaskillstimer.R;

import androidx.appcompat.widget.AppCompatImageView;

public class AspectRatioImageView extends AppCompatImageView {

    private static final float DEFAULT_ASPECT_RATIO = 1.77f;

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    private float aspectRatio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a =context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, DEFAULT_ASPECT_RATIO);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth;
        int newHeight;
        newWidth=getMeasuredWidth();
        if(aspectRatio==0.0f){
            aspectRatio=DEFAULT_ASPECT_RATIO;
        }
        newHeight=(int)(newWidth/aspectRatio);
        setMeasuredDimension(newWidth,newHeight);

    }
}
