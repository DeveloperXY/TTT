package com.spartech.ttt.model;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by Mohammed Aouf ZOUAG on 05/04/2016.
 */
public class Cells {
    /**
     * @return 9 empty cells, representing a new grid.
     */
    public static List<Cell> newEmptyGrid() {
        return Stream.generate(Cell::new)
                .limit(9)
                .collect(Collectors.toList());
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
     * @return the cell's string representation of its location [abc][012]
     */
    public static String getCellPositionBasedOnLocation(int location) {
        return location >= 0 && location < 3 ? ("a" + location) :
                location >= 3 && location < 6 ? ("b" + (location - 3)) :
                        ("c" + (location - 6));
    }
}
