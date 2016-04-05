package com.spartech.ttt.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spartech.ttt.R;
import com.spartech.ttt.socketio.TTTApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

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
