package com.spartech.ttt.gameutils;

/**
 * Created by Moham on 02/04/2016.
 *
 * The mark of a cell.
 */
public enum Mark {
    X("X"), O("O"), EMPTY("");

    private String mark;

    Mark(String s) {
        mark = s;
    }

    @Override
    public String toString() {
        return mark;
    }
}
