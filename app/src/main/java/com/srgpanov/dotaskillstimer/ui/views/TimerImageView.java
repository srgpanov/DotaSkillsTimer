package com.srgpanov.dotaskillstimer.ui.views;

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

import com.srgpanov.dotaskillstimer.R;
import com.srgpanov.dotaskillstimer.utils.TimerState;

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
    private int countTimerValueRemaining;
    private boolean timerIsRunning;
    private float pastTimerArc;
    private ValueAnimator timerAnimator;
    private PropertyValuesHolder propertyValuesTime;
    private String abilityName;

    private int timerDuration;



    private long finishTime;
    public TimerImageView(Context context) {
        super(context);
        init();
    }
    public TimerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimerImageView);
        timerDuration = typedArray.getInt(R.styleable.TimerImageView_timer_milisec, 5000);
        typedArray.recycle();
        init();
    }


    public TimerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getTimerDuration() {
        return timerDuration;
    }

    public void setTimerDuration(int timerDuration) {
        this.timerDuration = timerDuration;
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
        timerAnimator.setInterpolator(new LinearInterpolator());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        logged("onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setTimer() {
        int timeRemaining =(int) (finishTime-System.currentTimeMillis());
        propertyValuesTime = PropertyValuesHolder.ofInt(PROPERTY_TIME, timeRemaining, 0);
        timerAnimator.setValues( propertyValuesTime);
        timerAnimator.setDuration(timeRemaining);
        timerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                timerIsRunning = false;
                invalidate();
            }
        });
        timerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int msesRemaining=(int) animation.getAnimatedValue(PROPERTY_TIME);
                float partOfDuration =timerDuration/(float)msesRemaining;
                pastTimerArc =360- (360/partOfDuration);
                int secondsRemaining = msesRemaining / 1000;
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
            setTimer();
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
            finishTime=System.currentTimeMillis()+timerDuration;
            setTimer();
            timerIsRunning = true;
            timerAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (timerIsRunning) {
            super.onDraw(canvas);
            drawTimerArc(canvas, pastTimerArc);
            drawTimerCount(canvas, countTimerValueRemaining);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setTimerState(TimerState state){
        logged("setTimerState");
        this.timerIsRunning=state.isTimerIsRunning();
        this.finishTime=state.getFinishTime();
    }
    public TimerState getTimerState(){
        return new TimerState(finishTime,timerIsRunning);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(timerAnimator!=null){
            timerAnimator.cancel();
            timerAnimator=null;
        }
        super.onDetachedFromWindow();
    }

    private void drawTimerArc(Canvas canvas, float angle) {
        canvas.drawArc(timerArcRect, 270, angle - 360, true, timerPaint);
    }

    private void drawTimerCount(Canvas canvas, int leftTime) {
        canvas.drawText(String.valueOf(leftTime+1), centerHorizontal, yCenterText, textTimerPaint);
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
        finishTime=ss.finishTime;
        timerIsRunning= ss.timerIsRunning == 1;
    }
    private  class SavedState extends BaseSavedState {

        public  final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private long finishTime;
        private int timerIsRunning;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            finishTime = in.readLong();
            timerIsRunning = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(finishTime);
            out.writeInt(timerIsRunning);
        }
    }

}
