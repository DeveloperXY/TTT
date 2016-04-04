package com.spartech.ttt.socketio;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Moham on 04/04/2016.
 */
public class TTTApplication extends Application {
    private static final String SERVER_IP = "192.168.173.1";
    private static final String SERVER_PORT = "3000";
    private static final String SERVER_IP_ADDRESS =
            String.format("%s:%s", SERVER_IP, SERVER_PORT);
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mSocket = IO.socket(SERVER_IP_ADDRESS);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
