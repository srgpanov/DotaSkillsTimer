package com.example.dotaskillstimer.utils;

public class TimerHelper {
    private String name;
    private long startTime;
    private long finishTime;

    public TimerHelper(String name, long startTime, long finishTime) {
        this.name = name;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }
}
