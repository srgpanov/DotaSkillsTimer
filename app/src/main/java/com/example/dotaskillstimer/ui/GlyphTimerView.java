package com.example.dotaskillstimer.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.utils.RoshanState;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GlyphTimerView extends View {
    private final String TAG = "GlyphTimerView";
    private final String PROPERTY_TIME = "PROPERTY_TIME";
    private final int GLYPH_COOLDOWN = 300000;

    private Paint timerPaint;
    private TextPaint textTimerPaint;
    private Paint paintTransparent;
    private Path path;

    private long startTime;
    private long finishTime;
    private boolean timerIsRunning;

    private int width, height;
    private int countTimerValueRemaining;
    private float remainingTimerArc;
    private ValueAnimator timerAnimator;
    private PropertyValuesHolder propertyValuesTime;
    private Drawable glyphDrawable;

    public GlyphTimerView(Context context) {
        super(context);
    }

    public GlyphTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GlyphTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GlyphTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth = widthMeasureSpec;
        setMeasuredDimension(newWidth, newWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);
    }

    private void init(int width, int height) {
        this.width = width;
        this.height = height;

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        timerPaint = new Paint();
        timerPaint.setColor(Color.BLACK);
        timerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        timerPaint.setAlpha(160);
        textTimerPaint = new TextPaint();
        textTimerPaint.setColor(Color.WHITE);
        textTimerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textTimerPaint.setStrokeWidth(2);
        textTimerPaint.setTextAlign(Paint.Align.LEFT);
        textTimerPaint.setSubpixelText(true);
        textTimerPaint.setTextSize(height / 3);


        paintTransparent = new Paint();
        paintTransparent.setAntiAlias(true);
        paintTransparent.setColor(0xff424242);
        paintTransparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        timerAnimator = new ValueAnimator();
        timerAnimator.setInterpolator(new LinearInterpolator());

        glyphDrawable = ContextCompat.getDrawable(getContext(), R.drawable.glyph_of_fortification);
        path = new Path();
        path.addArc(new RectF(-width, 0, width, height * 2), 0, -90);
        path.lineTo(width, 0);
        path.lineTo(width, -height);
        if(timerIsRunning){
            setupTimer((int) (finishTime-System.currentTimeMillis()));
            timerAnimator.start();
        }

    }
    public void startTimer() {
        Log.d(TAG, "start");
        if (timerIsRunning) {
            timerIsRunning = false;
            timerAnimator.cancel();
            invalidate();
        } else {
            timerIsRunning = true;
            setupTimer(GLYPH_COOLDOWN);
            timerAnimator.start();
        }
    }
    private void setupTimer(int millSec) {
        countTimerValueRemaining = millSec/1000;
        propertyValuesTime = PropertyValuesHolder.ofInt(PROPERTY_TIME, countTimerValueRemaining, 0);
        timerAnimator.setValues( propertyValuesTime);
        timerAnimator.setDuration(millSec);
        timerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                timerIsRunning = false;
                invalidate();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startTime = System.currentTimeMillis();
                finishTime = startTime + millSec;
            }
        });
        timerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int secondsRemaining = (int) animation.getAnimatedValue(PROPERTY_TIME);
                float arcCooficient = -1f*90f/300f;
                remainingTimerArc=secondsRemaining*arcCooficient;
                countTimerValueRemaining = secondsRemaining;
                invalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        glyphDrawable.setBounds(canvas.getClipBounds());
        glyphDrawable.draw(canvas);
        if (timerIsRunning){
            canvas.drawArc(-getWidth(), 0, getWidth(), getHeight() * 2, 0, remainingTimerArc, true, timerPaint);
            drawTimerCount(canvas,countTimerValueRemaining);
        }
        canvas.drawPath(path, paintTransparent);
    }
    private void drawTimerCount(Canvas canvas, int leftTime) {
        int minutes, seconds;
        minutes = leftTime / 60;
        seconds = leftTime % 60;
        String time;
        if (seconds >= 10) {
            time = minutes + ":" + seconds;
        } else {
            time = minutes + ":0" + seconds;
        }
        canvas.drawText(time, width * 0.1f, height * 0.9f, textTimerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point touchedPoint = new Point(Math.round(event.getX()), Math.round(event.getY()));
        if (isInsideArc(touchedPoint)) {
            return super.onTouchEvent(event);
        }

        return true;
    }
    private boolean isInsideArc(Point touchedPoint) {
        Point bottomLeftPoint = new Point(0, height);
        int distance = (int) Math.round(Math.pow(touchedPoint.x - bottomLeftPoint.x, 2) + Math.pow(touchedPoint.y - bottomLeftPoint.y, 2));
        logging("distance" + distance);
        if (distance < Math.pow(width, 2)) {
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        GlyphTimerView.SavedState ss = new GlyphTimerView.SavedState(superState);
        ss.startTime = startTime;
        ss.finishTime = finishTime;
        if (timerIsRunning) {
            ss.timerIsRunning = 1;
        } else {
            ss.timerIsRunning = 0;
        }
        return ss;
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        GlyphTimerView.SavedState ss = (GlyphTimerView.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        startTime = ss.startTime;
        finishTime=ss.finishTime;
        timerIsRunning= ss.timerIsRunning == 1;
    }

    private void logging(String message) {
        Log.d(TAG, message);
    }
    private class SavedState extends BaseSavedState {

        public final Parcelable.Creator<GlyphTimerView.SavedState> CREATOR
                = new Parcelable.Creator<GlyphTimerView.SavedState>() {
            public GlyphTimerView.SavedState createFromParcel(Parcel in) {
                return new GlyphTimerView.SavedState(in);
            }

            public GlyphTimerView.SavedState[] newArray(int size) {
                return new GlyphTimerView.SavedState[size];
            }
        };
        private int timerIsRunning;
        private long startTime;
        private long finishTime;


        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            startTime = in.readLong();
            finishTime = in.readLong();
            timerIsRunning = in.readInt();

        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(startTime);
            out.writeLong(finishTime);
            out.writeInt(timerIsRunning);

        }
    }
}
