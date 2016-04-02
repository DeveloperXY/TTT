package com.spartech.ttt.model;

/**
 * Created by Moham on 02/04/2016.
 */
public class Cell {
    private boolean state;

    public Cell() {
        this(false);
    }

    public Cell(boolean state) {
        this.state = state;
    }
}
