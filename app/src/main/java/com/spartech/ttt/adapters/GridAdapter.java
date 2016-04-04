package com.spartech.ttt.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.spartech.ttt.R;
import com.spartech.ttt.gameutils.Mark;
import com.spartech.ttt.model.Cell;

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
        String position = Cell.getCellPositionBasedOnLocation(location);
        markCell(mark, position);
    }

    public void markCell(Mark mark, String position) {
        Cell cell = mCells.get(Cell.getCellLocationBasedOnPosition(position));
        cell.setMark(mark);
        notifyDataSetChanged();
    }

    /**
     * Resets all the cells of the game grid.
     */
    public void reset() {
        mCells.clear();
        mCells.addAll(Stream.generate(Cell::new)
                .limit(9)
                .collect(Collectors.toList()));
        notifyDataSetChanged();
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
