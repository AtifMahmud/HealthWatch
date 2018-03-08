package com.cpen391.healthwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.implementation.AppControl;
import com.cpen391.healthwatch.server.implementation.ServerContact;
import com.cpen391.healthwatch.util.GlobalFactory;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initClasses();
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
