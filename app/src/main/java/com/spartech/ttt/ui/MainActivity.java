package com.spartech.ttt.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.gameutils.Mark;
import com.spartech.ttt.model.Cells;
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

    @Bind(R.id.rematchButton)
    Button rematchButton;

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
    private TTTApplication mApplication;
    private AlertDialog mAlertDialog;

    /**
     * This flag indicates whether we would be switching to another activity or not.
     * If this flag is set to true, then that would mean that we should not stop
     * the audio playback.
     */
    private boolean isSwitching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mApplication = (TTTApplication) getApplication();
        isSwitching = false;

        setupGameGrid();
        setupGameSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplication.resumeMediaPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSwitching)
            mApplication.pauseMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        isSwitching = true;
        super.onBackPressed();
    }

    private void setupGameSocket() {
        mSocket = ((TTTApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Events.GAME_BEGIN, onGameBegin);
        mSocket.on(Events.OPPONENT_QUIT, onOpponentQuit);
        mSocket.on(Events.MOVE_MADE, onMoveMade);
        mSocket.on(Events.INCOMING_REMATCH_REQUEST, onRematchRequest);
        mSocket.on(Events.REMATCH_REJECTED, onRematchRejected);

        mSocket.connect();
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
        mSocket.off(Events.INCOMING_REMATCH_REQUEST, onRematchRequest);
        mSocket.off(Events.REMATCH_REJECTED, onRematchRejected);
    }

    /**
     * Invoked by a press on the 'Rematch' button.
     *
     * @param view
     */
    public void onRematch(View view) {
        mSocket.emit(Events.DEMAND_REMATCH);
        Snackbar.make(getWindow().getDecorView(),
                "Rematch request sent.", Snackbar.LENGTH_LONG).show();
        rematchButton.setEnabled(false);
        statusLabel.setText("Waiting for your opponent's response...");
    }

    /**
     * Initial setup of the game board.
     */
    private void setupGameGrid() {
        mGridAdapter = new GridAdapter(this, Cells.newEmptyGrid());
        mGridAdapter.setGridListener(new GridAdapter.GridListener() {
            @Override
            public boolean isMyTurn() {
                return myTurn;
            }

            @Override
            public void onCellClicked(int location) {
                mGridAdapter.markCell(mSymbol, location);
                try {
                    makeMove(Cells.getCellPositionBasedOnLocation(location));
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
                        rematchButton.setEnabled(true);
                        rematchButton.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Starting game !",
                                Toast.LENGTH_LONG).show();
                        retrieveSymbolFromResponse(args);
                        initializeGameBoard();
                    });

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
     * A listener that fires when the opponent player sends a rematch request.
     */
    private Emitter.Listener onRematchRequest =
            args -> runOnUiThread(this::displayRequestNotificationDialog);

    /**
     * A listener that fires once a player makes a move on the grid.
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
                                checkIfGameWasOver();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

    /**
     * A listener that fires if the opponent player rejected the sent rematch request.
     */
    private Emitter.Listener onRematchRejected =
            args -> runOnUiThread(
                    () -> {
                        Snackbar.make(getWindow().getDecorView(),
                                "Your rematch request was rejected.",
                                Snackbar.LENGTH_LONG).show();

                        mGridAdapter.reset();
                        statusLabel.setText("Waiting for opponent...");
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
        mGridAdapter.reset();
        myTurn = "X".equals(mSymbol.toString());
        statusLabel.setText(myTurn ? "Your turn." : "Waiting for your opponent's move...");
    }

    /**
     * Displays a dialog, notifying the user of an incoming rematch request.
     */
    private void displayRequestNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Rematch request");
        builder.setMessage("Your opponent is asking for a rematch ! How will you respond ?");
        builder.setNegativeButton("Not now",
                (dialog, which) -> {
                    acceptRematchRequest(false);
//                    Tasks.closeActivityAfterDelay(this);
                })
                .setPositiveButton("Yeah, why not",
                        (dialog, which) -> acceptRematchRequest(true));

        mAlertDialog = builder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }

    private void checkIfGameWasOver() {
        String statusMessage;

        if (isGameOver()) {
            statusMessage = myTurn ? "Game over. You lost." : "Game over. You WON !";
            rematchButton.setVisibility(View.VISIBLE);
        } else {
            if (mGridAdapter.isGridFull()) {
                // This game is a draw
                statusMessage = "Close one, It's a DRAW !";
            } else {
                // The game isn't over yet
                statusMessage = myTurn ? "Your turn." :
                        "Waiting for your opponent's move...";
            }
        }

        statusLabel.setText(statusMessage);
    }

    private boolean isGameOver() {
        return mGridAdapter.isGameOver();
    }

    /**
     * Responds to the incoming rematch request.
     *
     * @param action to take (true: accept / false: deny)
     */
    private void acceptRematchRequest(boolean action) {
        Socket socket = ((TTTApplication) getApplication()).getSocket();

        try {
            JSONObject params = new JSONObject();
            params.put("response", action);
            socket.emit("rematchResponse", params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
