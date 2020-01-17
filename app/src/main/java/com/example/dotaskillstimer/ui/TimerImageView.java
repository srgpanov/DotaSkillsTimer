package com.example.dotaskillstimer.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.example.dotaskillstimer.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class TimerImageView extends AppCompatImageView {
    private final String TAG = "TimerImageView";
    private final String PROPERTY_TIME = "PROPERTY_TIME";
    private final String PROPERTY_ANGLE = "PROPERTY_ANGLE";
    private float centerVertical;
    private float centerHorizontal;
    private int width;
    private int height;
    private Paint timerPaint;
    private Paint textTimerPaint;
    private RectF timerArcRect;
    private float yCenterText;
    private Rect textBounds;
    private int countTimerValueStarted;
    private int countTimerValueRemaining;
    private int countTimerValueSeconds = countTimerValueStarted / 1000;
    private boolean timerIsRunning;
    private float pastTimerArc;
    private ValueAnimator timerAnimator;
    private PropertyValuesHolder propertyValuesTime;
    private PropertyValuesHolder propertyValuesAngle;
    private String abilityName;

    private int invalidateCounter;

    private long currentTime;
    private long startTime;
    private long finishTime;
    public TimerImageView(Context context) {
        super(context);
        init();
    }

    public TimerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimerImageView);
        countTimerValueStarted = typedArray.getInt(R.styleable.TimerImageView_timer_milisec, 5000);
        typedArray.recycle();
        init();
    }


    public TimerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean isTimerIsRunning() {
        return timerIsRunning;
    }

    private void init() {
        timerPaint = new Paint();
        timerPaint.setColor(Color.BLACK);
        timerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timerPaint.setAlpha(160);
        textTimerPaint = new Paint();
        textTimerPaint.setColor(Color.WHITE);
        textTimerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        timerIsRunning = false;
        textTimerPaint.setStrokeWidth(3);
        textTimerPaint.setTextAlign(Paint.Align.CENTER);
        timerAnimator = new ValueAnimator();
        propertyValuesAngle = PropertyValuesHolder.ofInt(PROPERTY_ANGLE, 0, 360);
        timerAnimator.setInterpolator(new LinearInterpolator());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        logged("onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    public void setTimer(int millSec) {

        countTimerValueStarted = millSec;
        countTimerValueRemaining = countTimerValueStarted;

        propertyValuesTime = PropertyValuesHolder.ofInt(PROPERTY_TIME, countTimerValueStarted, 0);
        timerAnimator.setValues(propertyValuesAngle, propertyValuesTime);
        timerAnimator.setDuration(countTimerValueStarted);
        timerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                logged("duration " + (System.currentTimeMillis() - currentTime));
                timerIsRunning = false;
                invalidate();
                logged("invalidateCounter " + invalidateCounter);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                currentTime = System.currentTimeMillis();
                startTime = System.currentTimeMillis();
                finishTime = startTime + millSec;
                logged("duration " + startTime + "  " + finishTime);
            }
        });
        timerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pastTimerArc = (int) animation.getAnimatedValue(PROPERTY_ANGLE);
                int secondsRemaining = (int) animation.getAnimatedValue(PROPERTY_TIME) / 1000;
                countTimerValueRemaining = secondsRemaining;
                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        logged("onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerVertical = (height) / 2;
        centerHorizontal = (width) / 2;
        timerArcRect = new RectF(0, 0, w, h);
        timerArcRect.inset(-0.3f * width, -0.3f * height);
        textTimerPaint.setTextSize(height / 2);
        textBounds = new Rect();
        textTimerPaint.getTextBounds("18", 0, "18".length(), textBounds);
        yCenterText = centerVertical + (Math.abs(textBounds.height())) / 2;
        if(timerIsRunning){
            logged("rotate");
            setTimer((int) (finishTime-System.currentTimeMillis()));
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
            timerAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        logged("invalidate");
        invalidateCounter++;
        if (timerIsRunning) {
            super.onDraw(canvas);
            drawTimerArc(canvas, pastTimerArc);
            drawTimerCount(canvas, countTimerValueRemaining);
        } else {
            super.onDraw(canvas);
        }
    }

    public int getCountTimerValueStarted() {
        return countTimerValueStarted;
    }

    public void setCountTimerValueStarted(int countTimerValueStarted) {
        this.countTimerValueStarted = countTimerValueStarted;
    }

    private void drawTimerArc(Canvas canvas, float angle) {
        canvas.drawArc(timerArcRect, 270, angle - 360, true, timerPaint);
    }

    private void drawTimerCount(Canvas canvas, int leftTime) {
        canvas.drawText(String.valueOf(leftTime), centerHorizontal, yCenterText, textTimerPaint);
    }

    public void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }


    private void logged(String message) {
        Log.d(TAG, message);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        logged("onSaveInstanceState");
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
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
        logged("onRestoreInstanceState");
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        startTime = ss.startTime;
        finishTime=ss.finishTime;
        timerIsRunning= ss.timerIsRunning == 1;
    }
    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private long startTime;
        private long finishTime;
        private int timerIsRunning;

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
