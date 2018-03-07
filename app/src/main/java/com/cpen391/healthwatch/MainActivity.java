package com.cpen391.healthwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.implementation.AppControl;
import com.cpen391.healthwatch.server.implementation.ServerContact;
import com.cpen391.healthwatch.util.GlobalFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initClasses();
        test();
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
