/*
 * This is the activity to launch the maps activity. The activity is not meant to belong in the main project as a functional activity, rather it is
 * here to aid testing of the
 */

package com.cpen391.healthwatch;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cpen391.healthwatch.map.MapsActivity;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.implementation.AppControl;
import com.cpen391.healthwatch.server.implementation.ServerContact;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.example.atifm.healthwatch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initClasses();
        test();

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

    /**
     * Test server connection.
     */
    private void test() {
        GlobalFactory.getServerInterface().asyncGet("/gateway/auth",
                new ServerCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        Log.d("Main", "Obtained: " + response);
                    }
                });
    }

    /**
     * Initializes the implementations of the required interfaces.
     */
    private void initClasses() {
        AppControlInterface appControl = new AppControl(getApplicationContext());
        GlobalFactory.setAppControlInterface(appControl);
        GlobalFactory.setServerInterface(new ServerContact(appControl));
    }
}
