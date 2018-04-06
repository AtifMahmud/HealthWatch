package com.cpen391.healthwatch;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cpen391.healthwatch.map.MapActivity;
import com.cpen391.healthwatch.map.marker.animation.MarkerAnimationFactory;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.implementation.AppControl;
import com.cpen391.healthwatch.server.implementation.ServerContact;
import com.cpen391.healthwatch.user.LoginActivity;
import com.cpen391.healthwatch.user.UserSession;
import com.cpen391.healthwatch.util.GlobalFactory;

/**
 * The entry point into the "main" activity, this activity displays a splash screen
 * while the rest of the app starts up, transitioning to the "main" activity once
 * it is ready to go.
 */
public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getSimpleName();

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
    private boolean hasLoginToken() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(getString(R.string.login_token), "");
        if (!token.isEmpty()) {
            Log.d(TAG, "Token: " + token);
            // Should check token is valid, here, leaving for later.
            GlobalFactory.getUserSessionInterface().setUserToken(this, token);
            Log.d(TAG, "username: " + GlobalFactory.getUserSessionInterface().getUsername());
            Log.d(TAG, "user type: " + GlobalFactory.getUserSessionInterface().getUserType());
        }
        return !token.isEmpty();
    }

    private void advanceToMainActivity() {
        if (!hasLoginToken()) {
            Intent intent = new Intent(this, LoginActivity.class);
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
        GlobalFactory.setUserSessionInterface(new UserSession());
    }
}
