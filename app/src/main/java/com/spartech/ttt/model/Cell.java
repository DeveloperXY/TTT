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
        char numeric = position.charAt(1);
        switch (position.charAt(0)) {
            case 'a':
                return Character.getNumericValue(numeric);
            case 'b':
                return Character.getNumericValue(numeric) + 3;
            case 'c':
                return Character.getNumericValue(numeric) + 6;
        }

        return -1;
    }

    /**
     * @param location of the cell (1..9)
     * @return the cell's string representation of its location [abc][123]
     */
    public static String getCellPositionBasedOnLocation(int location) {
        return location >= 0 && location < 3 ? ("a" + location) :
                location >= 3 && location < 6 ? ("b" + (location - 3)) :
                        ("c" + (location - 6));
    }
}
