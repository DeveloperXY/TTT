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

    /**
     * @param position of the cell
     * @return its "numeric" location.
     */
    public static int getCellLocationBasedOnPosition(String position) {
        switch (position) {
            case "a0":
                return 0;
            case "a1":
                return 1;
            case "a2":
                return 2;
            case "b0":
                return 3;
            case "b1":
                return 4;
            case "b2":
                return 5;
            case "c0":
                return 6;
            case "c1":
                return 7;
            case "c2":
                return 8;
        }

        return -1;
    }
}
