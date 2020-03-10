package com.srgpanov.dotaskillstimer.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.srgpanov.dotaskillstimer.R;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TimeGameView extends View {
    private final String TAG = this.getClass().getSimpleName();
    int width, height;
    Path backgroundPath;
    Path moonPath;
    Path sunPath;
    TextPaint timePaint;
    Paint moonPaint;
    Paint sunPaint;
    Paint backgroundPaint;
    private int backgroundColor;
    private int moonColor;
    private boolean isDay = true;
    private String time;
    private long timeGameStarted;
    private Disposable disposableTime;


    public TimeGameView(Context context) {
        super(context);
    }

    public TimeGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimeGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newHeight = widthMeasureSpec / 2;
        setMeasuredDimension(widthMeasureSpec, newHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);

    }

    private void init(int w, int h) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        width = w;
        height = h;
        time = " ";
        backgroundColor = Color.parseColor("#3d3f45");
        moonColor = Color.parseColor("#00e2ff");
        sunPath = new Path();
        moonPath = new Path();
        sunPath.moveTo(-0.02f * height, -0.12f * height);
        sunPath.lineTo(0, -0.17f * height);
        sunPath.lineTo(0.02f * height, -0.12f * height);
        sunPath.close();
        Paint defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        defaultPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPath = new Path();
        backgroundPath.moveTo(0, height);
        backgroundPath.lineTo(0.1f * width, 0);
        backgroundPath.lineTo(0.9f * width, 0);
        backgroundPath.lineTo(width, height);
        backgroundPath.close();
        backgroundPaint = new Paint(defaultPaint);
        backgroundPaint.setColor(backgroundColor);
        timePaint = new TextPaint();
        timePaint.setAntiAlias(true);
        timePaint.setColor(Color.WHITE);
        timePaint.setTextSize(0.3f * height);
        timePaint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.radiance);
        Typeface typefaceBold = Typeface.create(typeface, Typeface.BOLD);
        logging(String.valueOf(typeface == null));
        timePaint.setTypeface(typefaceBold);
        moonPaint = new Paint(defaultPaint);
        moonPaint.setColor(moonColor);
        sunPaint = new Paint(defaultPaint);
        sunPaint.setColor(Color.YELLOW);
        if (timeGameStarted <= 0) {
            timeGameStarted = System.currentTimeMillis();
        }
        disposableTime = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                               @Override
                               public void accept(Long aLong) throws Exception {
                                   long time = System.currentTimeMillis() - timeGameStarted;
                                   int secondsFromStartGame = (int) (time / 1000);
                                   int minutes = secondsFromStartGame / 60;
                                   int seconds = secondsFromStartGame % 60;
                                   String gameTime;
                                   if (seconds >= 10) {
                                       gameTime = minutes + ":" + seconds;
                                   } else {
                                       gameTime = minutes + ":0" + seconds;
                                   }
                                   TimeGameView.this.time = gameTime;
                                   if ((minutes + 10) % 10 > 5) {
                                       isDay = false;
                                   } else {
                                       isDay = true;
                                   }
                                   invalidate();
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(backgroundPath, backgroundPaint);
        canvas.drawText(time, width / 2, height * 0.35f, timePaint);
        if (isDay) {
            drawSun(canvas);
        } else {
            drawMoon(canvas);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.startTime = timeGameStarted;
        if (isDay) {
            ss.isDay = 1;
        } else ss.isDay = 0;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        TimeGameView.SavedState ss = (TimeGameView.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        timeGameStarted = ss.startTime;
        isDay = ss.isDay == 1;

    }

    @Override
    protected void onDetachedFromWindow() {
        if(disposableTime!=null){
            disposableTime.dispose();
        }
        super.onDetachedFromWindow();
    }

    private void drawSun(Canvas canvas) {
        canvas.translate(0, height * 0.15f);
        canvas.translate(width / 2, height / 2);
        canvas.drawCircle(0, 0, 0.1f * height, sunPaint);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
        canvas.rotate(45f);
        canvas.drawPath(sunPath, sunPaint);
    }

    private void drawMoon(Canvas canvas) {
        moonPaint.setColor(moonColor);
        canvas.translate(0, height * 0.15f);
        canvas.drawCircle(width / 2, height / 2, 0.16f * height, moonPaint);
        moonPaint.setColor(backgroundColor);
        canvas.translate(0.08f * height, 0);
        moonPath.addCircle((width / 2f), height / 2, 0.14f * height, Path.Direction.CW);
        canvas.drawPath(moonPath, moonPaint);
    }

    private void logging(String message) {
        Log.d(TAG, message);
    }

    private class SavedState extends BaseSavedState {

        public final Parcelable.Creator<TimeGameView.SavedState> CREATOR
                = new Parcelable.Creator<TimeGameView.SavedState>() {
            public TimeGameView.SavedState createFromParcel(Parcel in) {
                return new TimeGameView.SavedState(in);
            }

            public TimeGameView.SavedState[] newArray(int size) {
                return new TimeGameView.SavedState[size];
            }
        };
        private long startTime;
        private int isDay;


        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            startTime = in.readLong();
            isDay = in.readInt();

        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(startTime);
            out.writeInt(isDay);
        }
    }
}
