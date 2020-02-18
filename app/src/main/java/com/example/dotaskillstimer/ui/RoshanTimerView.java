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
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.dotaskillstimer.R;
import com.example.dotaskillstimer.utils.RoshanState;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class RoshanTimerView extends View {
    private final String TAG = "RoshanImageView";
    private final String PROPERTY_TIME = "PROPERTY_TIME";
    private final int ROSHAN_RESPAWN = 660000;
    private Paint timerPaint;
    private Paint textTimerPaint;
    private Rect textBounds;

    private Paint paintTransparent;
    private int width, height;
    private int countTimerValueRemaining;
    private int remainingTimerArc;
    private ValueAnimator timerAnimator;
    private PropertyValuesHolder propertyValuesTime;
    private RoshanState roshanState;
    private long startTime;
    private long finishTime;

    private Path path;
    private Drawable roshanDrawable;
    private Drawable aegisDrawable;
    private Drawable deadDrawable;
    private Drawable deadOrLiveDrawable;
    //TODO: сделать нормальные картинки для состояний

    public RoshanTimerView(Context context) {
        super(context);
        init();
    }

    public RoshanTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoshanTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void logging(String message) {
        Log.d(TAG, message);
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        timerPaint = new Paint();
        timerPaint.setColor(Color.BLACK);
        timerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        timerPaint.setAlpha(160);
        textTimerPaint = new Paint();
        textTimerPaint.setColor(Color.WHITE);
        textTimerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textTimerPaint.setStrokeWidth(2);
        textTimerPaint.setTextAlign(Paint.Align.RIGHT);
        textTimerPaint.setSubpixelText(true);
        roshanState = RoshanState.ALIVE;
        paintTransparent = new Paint();
        paintTransparent.setAntiAlias(true);
        paintTransparent.setColor(0xff424242);
        paintTransparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        timerAnimator = new ValueAnimator();
        timerAnimator.setInterpolator(new LinearInterpolator());

        roshanDrawable = ContextCompat.getDrawable(getContext(), R.drawable.roshan);
        aegisDrawable = ContextCompat.getDrawable(getContext(), R.drawable.aegis);
        deadDrawable = ContextCompat.getDrawable(getContext(), R.drawable.dead);
        deadOrLiveDrawable = ContextCompat.getDrawable(getContext(), R.drawable.dead_or_live);

    }

    private void setupTimer(int millSec) {
        int timeRemaining;
        if (millSec < 0){
            timeRemaining=ROSHAN_RESPAWN;
        }
        else {
            timeRemaining=millSec;
        }
        propertyValuesTime = PropertyValuesHolder.ofInt(PROPERTY_TIME, timeRemaining / 1000, 0);
        timerAnimator.setValues(propertyValuesTime);
        timerAnimator.setDuration(timeRemaining);
        timerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (roshanState != RoshanState.ALIVE) roshanState = RoshanState.ALIVE;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                invalidate();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startTime = System.currentTimeMillis();
                finishTime = startTime + timeRemaining;
            }
        });
        timerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int secondsRemaining = (int) animation.getAnimatedValue(PROPERTY_TIME);

                if (secondsRemaining > 360 && roshanState == RoshanState.AEGIS) {
                    countTimerValueRemaining = secondsRemaining - 360;
                } else {
                    if (secondsRemaining > 180) {
                        roshanState = RoshanState.DEAD;
                        countTimerValueRemaining = secondsRemaining - 180;
                    } else {
                        roshanState = RoshanState.DEAD_OR_LIVE;
                        countTimerValueRemaining = secondsRemaining;
                    }


                }
                switch (roshanState) {
                    case ALIVE:
                        remainingTimerArc = 0;
                        break;
                    case AEGIS:
                        float angleCoefficient = 300f / countTimerValueRemaining;
                        remainingTimerArc = (int) (90f / angleCoefficient);
                        break;
                    case DEAD:
                        remainingTimerArc = (int) (90f / (480f / countTimerValueRemaining));
                        break;
                    case DEAD_OR_LIVE:
                        remainingTimerArc = (int) (90f / (180f / countTimerValueRemaining));
                        break;
                }
                logging(roshanState + " " + String.valueOf(countTimerValueRemaining) + "angle " + remainingTimerArc);


                invalidate();

            }
        });

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
        width = w;
        height = h;
        textTimerPaint.setTextSize(h / 3);
        textBounds = new Rect();
        textTimerPaint.getTextBounds("08:28", 0, "08:28".length(), textBounds);
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, -height);
        path.addArc(new RectF(0, 0, width * 2, height * 2), 180, 90);
        path.lineTo(0, 0);
        path.close();
        setupTimer((int) (finishTime - System.currentTimeMillis()));
        if (roshanState != RoshanState.ALIVE) {
            timerAnimator.start();
        }
    }

    public void startTimer() {
        switch (roshanState) {
            case ALIVE:
                switchRoshanState();
                setupTimer(ROSHAN_RESPAWN);
                timerAnimator.start();
                break;
            case AEGIS:
                switchRoshanState();
                break;
            case DEAD:
            case DEAD_OR_LIVE:
                roshanState = RoshanState.ALIVE;
                timerAnimator.cancel();
                break;
        }
        Log.d(TAG, String.valueOf(roshanState));
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
        Point bottomRightPoint = new Point(width, height);
        int distance = (int) Math.round(Math.pow(touchedPoint.x - bottomRightPoint.x, 2) + Math.pow(touchedPoint.y - bottomRightPoint.y, 2));
        logging("distance" + distance);
        if (distance < Math.pow(width, 2)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        switch (roshanState) {
            case ALIVE:
                drawRoshanLive(canvas);
                break;
            case AEGIS:
                drawAegis(canvas);
                canvas.drawArc(0, 0, getWidth() * 2, getHeight() * 2, 180, remainingTimerArc, true, timerPaint);
                drawTimerCount(canvas, countTimerValueRemaining);
                break;
            case DEAD:
                drawRoshanDead(canvas);
                canvas.drawArc(0, 0, getWidth() * 2, getHeight() * 2, 180, remainingTimerArc, true, timerPaint);
                drawTimerCount(canvas, countTimerValueRemaining);
                break;
            case DEAD_OR_LIVE:
                drawRoshanDeadOrLive(canvas);
                canvas.drawArc(0, 0, getWidth() * 2, getHeight() * 2, 180, remainingTimerArc, true, timerPaint);
                drawTimerCount(canvas, countTimerValueRemaining);
                break;
        }
        canvas.drawPath(path, paintTransparent);

    }

    private void drawRoshanDeadOrLive(Canvas canvas) {
        deadOrLiveDrawable.setBounds(canvas.getClipBounds());
        deadOrLiveDrawable.draw(canvas);
    }

    private void drawRoshanDead(Canvas canvas) {
        deadDrawable.setBounds(canvas.getClipBounds());
        deadDrawable.draw(canvas);
    }

    private void drawAegis(Canvas canvas) {
        logging("draw aegis");
        aegisDrawable.setBounds(canvas.getClipBounds());
        aegisDrawable.draw(canvas);
    }

    private void drawRoshanLive(Canvas canvas) {
        logging("draw roshan");
        roshanDrawable.setBounds(canvas.getClipBounds());
        roshanDrawable.draw(canvas);
    }

    private void switchRoshanState() {
        switch (roshanState) {
            case ALIVE:
                roshanState = RoshanState.AEGIS;
                break;
            case AEGIS:
                roshanState = RoshanState.DEAD;
                break;
            case DEAD:
                roshanState = RoshanState.DEAD_OR_LIVE;
                break;
            case DEAD_OR_LIVE:
                roshanState = RoshanState.ALIVE;
                break;
        }
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
        canvas.drawText(time, width * 0.9f, height * 0.9f, textTimerPaint);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.startTime = startTime;
        ss.finishTime = finishTime;
        ss.roshanState = roshanState;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        startTime = ss.startTime;
        finishTime = ss.finishTime;
        roshanState = ss.roshanState;
        logging("restore" + roshanState);
    }

    private class SavedState extends BaseSavedState {

        public final Parcelable.Creator<RoshanTimerView.SavedState> CREATOR
                = new Parcelable.Creator<RoshanTimerView.SavedState>() {
            public RoshanTimerView.SavedState createFromParcel(Parcel in) {
                return new RoshanTimerView.SavedState(in);
            }

            public RoshanTimerView.SavedState[] newArray(int size) {
                return new RoshanTimerView.SavedState[size];
            }
        };
        private RoshanState roshanState;
        private long startTime;
        private long finishTime;


        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            startTime = in.readLong();
            finishTime = in.readLong();
            roshanState = RoshanState.valueOf(in.readString());

        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(startTime);
            out.writeLong(finishTime);
            out.writeString(roshanState.name());

        }
    }
}

