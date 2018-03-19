package com.cpen391.healthwatch;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen391.healthwatch.Caretaker.CareTakerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button caretakerButton = (Button) findViewById(R.id.test_activity_button);
        caretakerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showCareTakerPage();
            }
        });
    }

    public void showCareTakerPage(){
        Intent careTakerIntent = new Intent(this, CareTakerActivity.class);
        startActivity(careTakerIntent);
    }


}
