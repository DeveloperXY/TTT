package com.spartech.ttt.gameutils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mohammed Aouf ZOUAG on 05/04/2016.
 */
public class Tasks {
    private static ScheduledExecutorService executor;

    static {
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static void closeActivityAfterDelay(Context context) {
        executor.schedule(delayedFinish(context), 3, TimeUnit.SECONDS);
    }

    /**
     * @param context of the concerned activity
     * @return a new Runnable object, that terminates the passed-in activity.
     */
    private static Runnable delayedFinish(Context context) {
        return ((AppCompatActivity) context)::finish;
    }
}
