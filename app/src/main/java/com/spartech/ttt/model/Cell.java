package com.spartech.ttt.model;

import com.spartech.ttt.gameutils.Mark;

/**
 * Created by Mohammed Aouf ZOUAG on 02/04/2016.
 * <p>
 * Represents a game cell.
 */
public class Cell {
    private Mark mark;

    public Cell() {
        this(Mark.EMPTY);
    }

    public Cell(Mark mark) {
        this.mark = mark;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public boolean isEmpty() {
        return mark == Mark.EMPTY;
    }

    @Override
    public String toString() {
        return mark.toString();
    }
}
