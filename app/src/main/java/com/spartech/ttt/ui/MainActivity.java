package com.spartech.ttt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockApplication;
import android.widget.GridView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.model.Cell;
import com.spartech.ttt.socketio.TTTApplication;

import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;

    /**
     * The main game board, containing the cells.
     */
    @Bind(R.id.cellsGridview)
    GridView cellsGridview;

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
    }
}
