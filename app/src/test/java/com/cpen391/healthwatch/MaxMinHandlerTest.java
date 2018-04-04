package com.cpen391.healthwatch;

import android.content.Context;

import com.cpen391.healthwatch.patient.MaxMinHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by atifm on 4/4/2018.
 *
 */
@RunWith(JUnit4.class)
public class MaxMinHandlerTest {
    private Context mContext = mock(Context.class);

    private MaxMinHandler mMaxMinHandler;

    private class MaxMinHandlerTestClass extends MaxMinHandler {
        public MaxMinHandlerTestClass(Context context) {
            super(context);
        }

        void initTest() {
            maxBPM = "200";
            minBPM = "100";
            recentBPM = "75";
        }
    }


    @Before
    public void initTest() {
        MaxMinHandlerTestClass temp = new MaxMinHandlerTestClass(mContext);
        temp.initTest();
        mMaxMinHandler = temp;
    }

    @Test
    public void testMinBPM(){
        String minBPM = "50";
        String recentBPM = "60";
        String maxBPM = "100";

        long minBPMtime = 121121212;
        long maxBPMtime = 121121212;
        long recentBPMtime = 121121212;


        mMaxMinHandler.update("50", 10101010);
        assertEquals(minBPM, mMaxMinHandler.getMin());

    }

    @Test
    public void testMinBPMTime(){
        String minBPM = "50";
        String recentBPM = "60";
        String maxBPM = "100";

        long minBPMtime = 1000;
        long maxBPMtime = 1000;
        long recentBPMtime = 1000;


        mMaxMinHandler.update("40", 1500);
        assertEquals("40", mMaxMinHandler.getMin());

    }

}
