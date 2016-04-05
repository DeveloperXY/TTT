package com.spartech.ttt.gameutils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mohammed Aouf ZOUAG on 05/04/2016.
 */
public class Tasks {
    /**
     * @param context of the concerned activity
     * @return a new Runnable object, that terminates the passed-in activity.
     */
    public static Runnable delayedFinish(Context context) {
        return ((AppCompatActivity) context)::onBackPressed;
    }
}
