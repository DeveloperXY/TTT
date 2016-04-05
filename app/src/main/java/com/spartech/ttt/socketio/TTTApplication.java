package com.spartech.ttt.socketio;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.gameutils.Constants;

import java.net.URISyntaxException;

/**
 * Created by Mohammed Aouf ZOUAG on 04/04/2016.
 */
public class TTTApplication extends Application {
    private Socket mSocket;
    private MediaPlayer mediaPlayer;
    /**
     * A boolean flag that indicates whther the media player had been released.
     */
    private boolean isReleased;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mSocket = IO.socket(Constants.SERVER_IP_ADDRESS);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        initializeSound();
        mediaPlayer.start();
        isReleased = false;
    }

    public void initializeSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.soundtrack);
        mediaPlayer.setLooping(true);
    }

    public void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    public void resumeMediaPlayer() {
        if (isReleased) {
            // The media player was released, re-initialize it
            isReleased = false;
            initializeSound();
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void killMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            isReleased = true;
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
