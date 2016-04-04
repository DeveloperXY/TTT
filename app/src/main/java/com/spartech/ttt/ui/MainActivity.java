package com.spartech.ttt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.gameutils.Mark;
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
    /**
     * A label to describe the current state of the game.
     */
    @Bind(R.id.statusLabel)
    TextView statusLabel;

    /**
     * The grid's adapter.
     */
    private GridAdapter mGridAdapter;
    private Socket mSocket;
    /**
     * The symbol of the current player.
     */
    private Mark mSymbol;
    /**
     * A boolean flag that indicates whether it's the current player's turn or not.
     */
    private boolean myTurn;

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
        mSocket.off(Events.OPPONENT_QUIT, onOpponentQuit);
        mSocket.off(Events.MOVE_MADE, onMoveMade);
    }

    /**
     * Initial setup of the game board.
     */
    private void setupGameGrid() {
        mGridAdapter = new GridAdapter(this,
                Stream.generate(Cell::new)
                        .limit(9)
                        .collect(Collectors.toList()));
        mGridAdapter.setGridListener(new GridAdapter.GridListener() {
            @Override
            public boolean isMyTurn() {
                return myTurn;
            }

            @Override
            public void onCellClicked(int location) {
                mGridAdapter.markCell(mSymbol, location);
                try {
                    makeMove(Cell.getCellPositionBasedOnLocation(location));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cellsGridview.setAdapter(mGridAdapter);
    }

    private void makeMove(String position) throws JSONException {
        JSONObject params = new JSONObject();
        params.put("symbol", mSymbol.toString());
        params.put("position", position);
        mSocket.emit(Events.MAKE_MOVE, params);
    }

    private void setupGameSocket() {
        mSocket = ((TTTApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Events.GAME_BEGIN, onGameBegin);
        mSocket.on(Events.OPPONENT_QUIT, onOpponentQuit);
        mSocket.on(Events.MOVE_MADE, onMoveMade);

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
                        retrieveSymbolFromResponse(args);
                        initializeGameBoard();
                    });

    /**
     * Retrieves the representative symbol of the current player.
     *
     * @param args the server's response
     */
    private void retrieveSymbolFromResponse(Object[] args) {
        try {
            mSymbol = ((JSONObject) args[0]).getString("symbol").equals("X") ?
                    Mark.X : Mark.O;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeGameBoard() {
        myTurn = "X".equals(mSymbol);
        statusLabel.setText(myTurn ? "Your turn." : "Waiting for your opponent's move...");
    }

    /**
     * A listener that fires once the opponent player quits the current game.
     */
    private Emitter.Listener onOpponentQuit =
            args -> runOnUiThread(
                    () -> {
                        Toast.makeText(MainActivity.this,
                                "Your opponent has quit the game !",
                                Toast.LENGTH_LONG).show();
                        statusLabel.setText("Waiting for opponent...");
                        mGridAdapter.reset();
                    });

    /**
     * A listener that fires once the opponent player quits the current game.
     */
    private Emitter.Listener onMoveMade =
            args -> runOnUiThread(
                    () -> {
                        try {
                            JSONObject data = ((JSONObject) args[0]);
                            Mark symbol = data.getString("symbol").equals("X") ?
                                    Mark.X : Mark.O;
                            String position = data.getString("position");

                            // Unknown bug, this listener is getting called twice
                            // First call: the 'position' string is valid,
                            // Second call: invalid 'position'
                            if (position.length() > 1) {
                                mGridAdapter.markCell(symbol, position);
                                myTurn = !mSymbol.equals(symbol);
                                statusLabel.setText(myTurn ? "Your turn." : "Waiting for your opponent's move...");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
}
