package com.spartech.ttt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.model.Cell;
import com.spartech.ttt.socketio.Events;
import com.spartech.ttt.socketio.TTTApplication;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /**
     * The main game board, containing the cells.
     */
    @Bind(R.id.cellsGridview)
    GridView cellsGridview;

    private Socket mSocket;
    /**
     * The symbol of the current player.
     */
    private String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupGameGrid();
        setupGameSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Events.GAME_BEGIN, onGameBegin);
    }

    /**
     * Initial setup of the game board.
     */
    private void setupGameGrid() {
        GridAdapter adapter = new GridAdapter(this,
                Stream.generate(Cell::new)
                        .limit(9)
                        .collect(Collectors.toList()));
        cellsGridview.setAdapter(adapter);
    }

    private void setupGameSocket() {
        mSocket = ((TTTApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Events.GAME_BEGIN, onGameBegin);

        mSocket.connect();
    }

    /**
     * A listener that fires in case the socket connection
     * could not be established (or timed out).
     */
    private Emitter.Listener onConnectError =
            args -> runOnUiThread(
                    () -> Toast.makeText(MainActivity.this,
                            "Cannot connect to server.",
                            Toast.LENGTH_LONG).show());

    /**
     * A listener that fires once a new game is about to begin.
     */
    private Emitter.Listener onGameBegin =
            args -> runOnUiThread(
                    () -> {
                        Toast.makeText(MainActivity.this,
                                "Starting game !",
                                Toast.LENGTH_LONG).show();
                        try {
                            mSymbol = ((JSONObject) args[0]).getString("symbol");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
}
