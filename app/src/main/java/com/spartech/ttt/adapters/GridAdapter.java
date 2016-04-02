package com.spartech.ttt.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.spartech.ttt.R;
import com.spartech.ttt.model.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moham on 02/04/2016.
 */
public class GridAdapter extends ArrayAdapter<Cell> {

    private Context mContext;
    private List<Cell> mCells;

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
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        Cell cell = mCells.get(position);
        viewHolder.textView.setTag("X");

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}
