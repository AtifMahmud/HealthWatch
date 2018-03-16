/**
 *
 * Activity for the patient activity page
 *
 * Sources:
 *      1. https://www.youtube.com/watch?v=InkQJ4riGyI
 *
 */


package com.cpen391.healthwatch.patient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.util.GlobalFactory;

public class PatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        TextView nameText = findViewById(R.id.user_profile_name);
        nameText.setText(GlobalFactory.getUserSessionInterface().getUsername());
    }
}
