package com.bignerdranch.android.criminalintent;

import org.junit.Test;

package com.example.android.testing.espresso.BasicSample;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


/**
 * Created by MarleyAlford on 10/24/16.
 */
public class MarleyEspressoTest {
    @Test
    public void pickCrime() {
        //choose a crime from the list
        onView(inAdapterView(withId(R.id.list_item_crime_title_text_view))
                .perform(click());
    }


}