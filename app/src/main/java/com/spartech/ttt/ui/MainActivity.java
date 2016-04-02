package com.spartech.ttt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.spartech.ttt.R;
import com.spartech.ttt.adapters.GridAdapter;
import com.spartech.ttt.model.Cell;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.cellsGridview)
    GridView cellsGridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridAdapter adapter = new GridAdapter(this,
                Stream.generate(Cell::new)
                        .limit(9)
                        .collect(Collectors.toList()));
        cellsGridview.setAdapter(adapter);
    }
}
