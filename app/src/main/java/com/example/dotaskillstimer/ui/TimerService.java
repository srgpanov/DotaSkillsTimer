package com.example.dotaskillstimer.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimerService extends Service {
    private long currentTime;
    private long startTime;
    private long finishTime;
    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
