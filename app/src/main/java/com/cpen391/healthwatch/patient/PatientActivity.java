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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cpen391.healthwatch.R;

public class PatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
    }
}
