package com.cpen391.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen391.healthwatch.patient.PatientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button testpageButton = (Button) findViewById(R.id.testPatientPage);
        testpageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPatientPage();
            }
        });
    }

    private void showPatientPage(){
        Intent patientPageIntent = new Intent(this, PatientActivity.class);
        startActivity(patientPageIntent);
    }

}
