package com.spartech.ttt.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Mohammed Aouf ZOUAG on 04/04/2016.
 */
@RunWith(Parameterized.class)
public class ParametrizedCellTests {

    private String inputPosition;
    private int expectedLocation;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a0", 0},
                {"a1", 1},
                {"a2", 2},
                {"b0", 3},
                {"b1", 4},
                {"b2", 5},
                {"c0", 6},
                {"c1", 7},
                {"c2", 8}
        });
    }

    public ParametrizedCellTests(String input, int expected) {
        this.inputPosition = input;
        this.expectedLocation = expected;
    }

    @Test
    public void ValidCellPositionGivesAValidLocation() throws Exception {
        int location = Cell.getCellLocationBasedOnPosition(inputPosition);
        assertEquals(expectedLocation, location);
    }

    @Test
    public void ValidCellLocationGivesAValidCellPosition() throws Exception {
        String position = Cell.getCellPositionBasedOnLocation(expectedLocation);
        assertEquals(inputPosition, position);
    }
}