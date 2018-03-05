/**
 * This is the activity to launch the maps activity. The activity is not meant to belong in the main project as a functional activity, rather it is
 * here to aid testing of the
 */

package com.cpen391.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen391.healthwatch.map.MapsActivity;
import com.example.atifm.healthwatch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button testMapsButton = (Button) findViewById(R.id.testMap);
        testMapsButton.setOnClickListener(new View.OnClickListener(){

            // Navigate to the map activity
            public void onClick(View v){
                showMap();
            }
        });
    }

    // Navigate to the map activity
    public void showMap(){
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }
}
