package me.detj.timelapse.timer;

import android.os.Handler;

public class IntervalTimer {

    private static final int MINUTE_IN_MILLIS = 1000 * 60;

    private static final int WAITING = 0;
    private static final int RUNNING = 1;
    private static final int FINISHED = 2;

    private final Handler handler;
    private final Runnable taskRunnable;
    private final IntervalTask task;

    private int status;

    public IntervalTimer(IntervalTask intervalTask, final int intervalMinutes) {
        this.task = intervalTask;
        this.handler = new Handler();

        status = WAITING;

        taskRunnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                handler.postDelayed(taskRunnable, minutesToMillis(intervalMinutes));
            }
        };
    }

    private static int minutesToMillis(int mins) {
        return mins * MINUTE_IN_MILLIS;
    }

    public synchronized void start() {
        if (status == WAITING) {
            status = RUNNING;
            taskRunnable.run();
        }
    }

    public synchronized void stop() {
        if (status == RUNNING) {
            status = FINISHED;
            handler.removeCallbacks(taskRunnable);
        }
    }

}
