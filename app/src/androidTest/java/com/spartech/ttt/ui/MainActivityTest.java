package com.spartech.ttt.ui;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.spartech.ttt.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Mohammed Aouf ZOUAG on 07/04/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void CheckStatusLabelIsFirstShownWithTheCorrectText() throws Exception {
        onView(withId(R.id.statusLabel))
                .check(matches(withText("Waiting for opponent...")));
    }

    @Test
    public void RematchButtonIsNotDisplayedAtStartUp() throws Exception {
        onView(withId(R.id.rematchButton))
                .check(matches(not(isDisplayed())));
    }
}