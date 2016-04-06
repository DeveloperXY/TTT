package com.spartech.ttt.ui;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.gameutils.Mark;
import com.spartech.ttt.gameutils.Tasks;
import com.spartech.ttt.model.Cells;
import com.spartech.ttt.socketio.Events;
import com.spartech.ttt.socketio.TTTApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private ScheduledExecutorService executor;
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

        if (executor != null)
            executor.shutdown();
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

    private void setupGameSocket() {
        mSocket = ((TTTApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Events.GAME_BEGIN, onGameBegin);
        mSocket.on(Events.OPPONENT_QUIT, onOpponentQuit);
        mSocket.on(Events.MOVE_MADE, onMoveMade);
        mSocket.on(Events.INCOMING_REMATCH_REQUEST, onRematchRequest);

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
        myTurn = "X".equals(mSymbol.toString());
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
     * A listener that fires when the opponent player sends a rematch request.
     */
    private Emitter.Listener onRematchRequest =
            args -> runOnUiThread(
                    () -> {
                        Toast.makeText(MainActivity.this,
                                "Incoming rematch request !",
                                Toast.LENGTH_LONG).show();
                        createRequestNotification();
                    });

    private void createRequestNotification() {
        Intent acceptIntent = new Intent(this, NotificationActionService.class);
        acceptIntent.setAction("ACCEPT");
        PendingIntent piAccept = PendingIntent.getService(this, 0, acceptIntent, 0);

        Intent denyIntent = new Intent(this, NotificationActionService.class);
        acceptIntent.setAction("DENY");
        PendingIntent piDeny = PendingIntent.getService(this, 0, denyIntent, 0);

        Notification notification = new NotificationCompat.Builder(MainActivity.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rematch request")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your opponent is requesting a rematch. What is it going to be ?"))
                .addAction(R.drawable.ic_action_tick, "Accept", piAccept)
                .addAction(R.drawable.ic_action_cancel, "Deny", piDeny)
                .build();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(45600, notification);
    }

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

    private void checkIfGameWasOver() {
        // A boolean flag that indicates whether to terminate the activity
        // after a certain delay or not
        boolean delayedFinish = false;
        String statusMessage;

        if (isGameOver()) {
            statusMessage = myTurn ? "Game over. You lost." : "Game over. You WON !";
            delayedFinish = true;
        } else {
            if (mGridAdapter.isGridFull()) {
                // This game is a draw
                delayedFinish = true;
                statusMessage = "Close one, It's a DRAW !";
            } else {
                // The game isn't over yet
                statusMessage = myTurn ? "Your turn." :
                        "Waiting for your opponent's move...";
            }
        }

        statusLabel.setText(statusMessage);

        if (delayedFinish) {
            if (executor == null)
                executor = Executors.newSingleThreadScheduledExecutor();
//            executor.schedule(Tasks.delayedFinish(this), 3, TimeUnit.SECONDS);

        }
    }

    private boolean isGameOver() {
        return mGridAdapter.isGameOver();
    }

    private static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();

        }
    }
}
