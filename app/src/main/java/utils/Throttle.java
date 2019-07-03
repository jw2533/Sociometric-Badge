package utils;

import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

public class Throttle {
    private class CustomTimerTask extends TimerTask {
        @Override
        public void run() {
            synchronized (locker) {
                function.run();
                timer = null;
            }
        }
    }
    private int delay;
    private Runnable function;
    private Timer timer;
    private long lastRaise;
    private CustomTimerTask task;
    private Object locker;

    public Throttle(int delay, Runnable function) {
        this.delay = delay;
        this.function = function;
        timer = new Timer();
        locker = new Object();
        lastRaise = 0;
    }

    public void raise() {
        synchronized (locker) {
            Date now = new Date();
            long nowTime = now.getTime();
            if (lastRaise <= 0) {
                lastRaise = nowTime;
                task = new CustomTimerTask();
                timer.schedule(task, delay);
            } else {
                long timeRemaining = delay - (nowTime - lastRaise);
                if (timeRemaining <= 0 || timeRemaining > delay) {
                    if (task != null) {
                        task.cancel();
                        task = null;
                    }
                    lastRaise = nowTime;
                    function.run();
                } else {
                    lastRaise = nowTime;
                    if (task != null) {
                        task.cancel();
                        task = null;
                    }
                    task = new CustomTimerTask();
                    timer.schedule(task, timeRemaining);
                }
            }
        }
    }
}
