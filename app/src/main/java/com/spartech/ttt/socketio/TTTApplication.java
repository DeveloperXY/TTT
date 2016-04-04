package com.spartech.ttt.socketio;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.gameutils.Constants;

import java.net.URISyntaxException;

/**
 * Created by Moham on 04/04/2016.
 */
public class TTTApplication extends Application {
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mSocket = IO.socket(Constants.SERVER_IP_ADDRESS);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
