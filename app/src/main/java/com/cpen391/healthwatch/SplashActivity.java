package com.cpen391.healthwatch;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cpen391.healthwatch.map.MapActivity;
import com.cpen391.healthwatch.map.marker.animation.MarkerAnimationFactory;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.implementation.AppControl;
import com.cpen391.healthwatch.server.implementation.ServerContact;
import com.cpen391.healthwatch.user.SignUpActivity;
import com.cpen391.healthwatch.util.GlobalFactory;

/**
 * The entry point into the "main" activity, this activity displays a splash screen
 * while the rest of the app starts up, transitioning to the "main" activity once
 * it is ready to go.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initClasses();
        advanceToMainActivity();
    }

    /**
     * Checks to see whether the app should show user the login page.
     *
     * @return true if app should display login page for user, false otherwise.
     */
    private boolean shouldGoToLoginPage() {
        // returns true for testing.
        // Should check against shared preferences or something to determine the outcome.
        return true;
    }

    private void advanceToMainActivity() {
        if (shouldGoToLoginPage()) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Initializes the implementations of the required interfaces.
     */
    private void initClasses() {
        AppControlInterface appControl = new AppControl(getApplicationContext());
        GlobalFactory.setAppControlInterface(appControl);
        GlobalFactory.setServerInterface(new ServerContact(appControl));
        GlobalFactory.setAbstractMarkerAnimationFactory(new MarkerAnimationFactory());
    }
}
