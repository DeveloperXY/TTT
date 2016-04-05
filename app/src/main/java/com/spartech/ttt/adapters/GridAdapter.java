package com.spartech.ttt.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.spartech.ttt.R;
import com.spartech.ttt.gameutils.Mark;
import com.spartech.ttt.model.Cell;
import com.spartech.ttt.model.Cells;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammed Aouf ZOUAG on 02/04/2016.
 */
public class GridAdapter extends ArrayAdapter<Cell> {

    private Context mContext;
    private List<Cell> mCells;
    private GridListener gridListener;

    public GridAdapter(Context context, List<Cell> cells) {
        super(context, -1, cells);

        mContext = context;
        mCells = new ArrayList<>(cells);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((AppCompatActivity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.cell_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.markTextview);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        Cell cell = mCells.get(position);
        viewHolder.textView.setText(cell.getMark().toString());

        // Set a touch listener on the grid cells
        convertView.setOnTouchListener((v, event) -> {
            // If it's not the current player's turn, don't react
            // to any click events on the grid.
            if (!gridListener.isMyTurn())
                return true;

            if (cell.isEmpty()) {
                gridListener.onCellClicked(position);
                cell.setMark(Mark.O);
                viewHolder.textView.setText(cell.getMark().toString());
                return false;
            }

            return true;
        });

        return convertView;
    }

    /**
     * Marks a given cell with a given mark.
     *
     * @param mark     to be drawn on the concerned cell
     * @param position of the clicked-upon cell
     */
    public void markCell(String mark, String position) {
        Mark symbol = "O".equals(mark) ? Mark.O : Mark.X;
        markCell(symbol, position);
    }

    public void markCell(Mark mark, int location) {
        String position = Cells.getCellPositionBasedOnLocation(location);
        markCell(mark, position);
    }

    public void markCell(Mark mark, String position) {
        Cell cell = mCells.get(Cells.getCellLocationBasedOnPosition(position));
        cell.setMark(mark);
        notifyDataSetChanged();
    }

    /**
     * Resets all the cells of the game grid.
     */
    public void reset() {
        mCells.clear();
        mCells.addAll(Cells.newEmptyGrid());
        notifyDataSetChanged();
    }

    /**
     * @return true if one of the players has won, or false otherwise.
     */
    public boolean isGameOver() {
        String[] matches = {"XXX", "OOO"};
        String[] rows = {
                mCells.get(0).toString() + mCells.get(1).toString() + mCells.get(2).toString(),
                mCells.get(3).toString() + mCells.get(4).toString() + mCells.get(5).toString(),
                mCells.get(6).toString() + mCells.get(7).toString() + mCells.get(8).toString(),
                mCells.get(0).toString() + mCells.get(4).toString() + mCells.get(8).toString(),
                mCells.get(2).toString() + mCells.get(4).toString() + mCells.get(6).toString(),
                mCells.get(0).toString() + mCells.get(3).toString() + mCells.get(6).toString(),
                mCells.get(1).toString() + mCells.get(4).toString() + mCells.get(7).toString(),
                mCells.get(2).toString() + mCells.get(5).toString() + mCells.get(8).toString()
        };

        for (int i = 0; i < rows.length; i++) {
            if (rows[i].equals(matches[0]) || rows[i].equals(matches[1]))
                return true;
        }

        return false;
    }

    /**
     * @return true if there are no empty cells within the grid, false otherwise.
     */
    public boolean isGridFull() {
        return Stream.of(mCells)
                .allMatch(mark -> !mark.isEmpty());
    }

    private static class ViewHolder {
        TextView textView;
    }

    public void setGridListener(GridListener listener) {
        gridListener = listener;
    }

    public interface GridListener {
        boolean isMyTurn();

        void onCellClicked(int location);
    }
}
