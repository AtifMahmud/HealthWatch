package com.cpen391.healthwatch.patient;

import android.content.Context;
import android.content.SharedPreferences;

import com.cpen391.healthwatch.R;

/**
 * Created by atifm on 4/4/2018
 *
 * This class supports functionality for getting recent maximum and minimum heart rate (BPM) values based on the timestamp
 *
 * Source: https://developer.android.com/training/data-storage/shared-preferences.html#java
 *
 *
 */
public class MaxMinHandler {
    private static final int SECONDS_PER_HOUR = 3600;

    private SharedPreferences mBpmSharedPref;
    private Context mContext;

    protected String recentBPM;
    protected String maxBPM;
    protected String minBPM;

    private long recentBPMTime;
    private long maxBPMTime;
    private long minBPMTime;

    /**
     *
     * @param context: Application context
     */
    public MaxMinHandler(Context context){
        mContext = context;
        mBpmSharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    /**
     * Initializes the shared preferences file
     *
     */
    // The first bpm is both max and min
    public void init(){
        String defaultBPM = "0";
        long defaultTime = 0L;
        // check if it is in sharedpref
        recentBPM = mBpmSharedPref.getString(mContext.getString(R.string.bpm_most_recent), defaultBPM);
        maxBPM = mBpmSharedPref.getString(mContext.getString(R.string.bpm_max), defaultBPM);
        minBPM = mBpmSharedPref.getString(mContext.getString(R.string.bpm_min), defaultBPM);

        recentBPMTime = mBpmSharedPref.getLong(mContext.getString(R.string.bpm_most_recent_time), defaultTime);
        maxBPMTime = mBpmSharedPref.getLong(mContext.getString(R.string.bpm_max_time), defaultTime);
        minBPMTime = mBpmSharedPref.getLong(mContext.getString(R.string.bpm_min_time), defaultTime);

    }

    /**
     *
     * @return the maximum BPM from last hour
     */
    public String getMax(){
        return maxBPM;
    }

    /**
     *
     * @return the minimum BPM from last hour
     */
    public String getMin(){
        return minBPM;
    }

    /**
     * Updates bpm with data
     *
     *
     */
    public void update(String BPM, long timestamp){
        long currentTime = System.currentTimeMillis()/1000;

        recentBPM = BPM;
        recentBPMTime = timestamp;
        if (timestamp - currentTime >= SECONDS_PER_HOUR){
            maxBPM = BPM;
            maxBPMTime = timestamp;

            minBPM = BPM;
            minBPMTime = timestamp;
        }
        else {
            if (Integer.parseInt(BPM) < Integer.parseInt(minBPM)){
                minBPM = BPM;
                minBPMTime = timestamp;
            }
            if (Integer.parseInt(BPM) > Integer.parseInt(maxBPM)){
                maxBPM = BPM;
                maxBPMTime = timestamp;
            }
        }
    }

    /**
     *  Saves the data from memory to the shared preferences file
     */
    public void save(){
        SharedPreferences.Editor editor = mBpmSharedPref.edit();

        editor.putString(mContext.getString(R.string.bpm_most_recent), recentBPM);
        editor.putLong(mContext.getString(R.string.bpm_most_recent_time), recentBPMTime);

        editor.putString(mContext.getString(R.string.bpm_max), maxBPM);
        editor.putLong(mContext.getString(R.string.bpm_max_time), maxBPMTime);

        editor.putString(mContext.getString(R.string.bpm_min), minBPM);
        editor.putLong(mContext.getString(R.string.bpm_min_time), minBPMTime);

        editor.apply();
    }
}