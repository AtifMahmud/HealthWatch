package com.cpen391.healthwatch.caretaker;

import android.app.Activity;
import android.os.Bundle;

import com.cpen391.healthwatch.R;

/**
 * Created by william on 2018-04-04.
 * This activity allows patients to view a caretaker's profile.
 */
public class CareTakerProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_taker_profile);
    }
}
