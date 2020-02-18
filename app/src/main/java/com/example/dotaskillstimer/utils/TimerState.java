package com.example.dotaskillstimer.utils;

import android.os.Parcel;
import android.os.Parcelable;


    public class TimerState implements Parcelable {
        private long finishTime;
        private boolean timerIsRunning;

        public TimerState(long finishTime, boolean timerIsRunning) {
            this.finishTime = finishTime;
            this.timerIsRunning = timerIsRunning;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }

        public boolean isTimerIsRunning() {
            return timerIsRunning;
        }

        public void setTimerIsRunning(boolean timerIsRunning) {
            this.timerIsRunning = timerIsRunning;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.finishTime);
            dest.writeByte(this.timerIsRunning ? (byte) 1 : (byte) 0);
        }

        protected TimerState(Parcel in) {
            this.finishTime = in.readLong();
            this.timerIsRunning = in.readByte() != 0;
        }

        public static final Parcelable.Creator<TimerState> CREATOR = new Parcelable.Creator<TimerState>() {
            @Override
            public TimerState createFromParcel(Parcel source) {
                return new TimerState(source);
            }

            @Override
            public TimerState[] newArray(int size) {
                return new TimerState[size];
            }
        };
    }

