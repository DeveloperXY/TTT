package com.spartech.ttt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.spartech.ttt.R;
import com.spartech.ttt.socketio.TTTApplication;

public class HomeActivity extends AppCompatActivity {

    private TTTApplication mApplication;
    /**
     * This flag indicates whether we would be switching to another activity or not.
     * If this flag is set to true, then that would mean that we should not stop
     * the audio playback.
     */
    private boolean isSwitching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mApplication = (TTTApplication) getApplication();
        isSwitching = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isSwitching = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplication.resumeMediaPlayer();
        isSwitching = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSwitching)
            mApplication.pauseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isSwitching)
            mApplication.killMediaPlayer();
    }

    public void onPlay(View view) {
        isSwitching = true;
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onQuit(View view) {
        finish();
    }
}
