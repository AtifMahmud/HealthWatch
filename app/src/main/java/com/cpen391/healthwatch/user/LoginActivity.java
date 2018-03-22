package com.cpen391.healthwatch.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.map.MapActivity;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by william on 2018/3/12.
 *
 */
public class LoginActivity extends Activity
        implements ServerCallback, ServerErrorCallback {
    private String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGN_UP = 0;

    private EditText mUsernameText;
    private EditText mPasswordText;
    private CircularProgressButton mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        obtainViews();
        setListeners();
    }

    private void obtainViews() {
        mUsernameText = findViewById(R.id.input_username);
        mPasswordText = findViewById(R.id.input_password);
        mLoginButton = findViewById(R.id.btn_login);
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        TextView signUpLink = findViewById(R.id.link_sign_up);
        signUpLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGN_UP);
            }
        });
    }

    private void login() {
        mUsernameText.setError(null);
        mPasswordText.setError(null);
        mLoginButton.setEnabled(false);
        mLoginButton.startAnimation();

        String body = validate();
        if (body == null) {
            mLoginButton.revertAnimation();
            mLoginButton.setEnabled(true);
            return;
        }
        GlobalFactory.getServerInterface().asyncPost("/gateway/auth/token", null,
                body, this, this);
    }

    /**
     * Validates the username and password that is inputted.
     *
     * @return the json object to send to the server if everything is entered correctly,
     *  or null if something was not entered correctly.
     */
    private String validate() {
        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        boolean error = false;
        if ("".equals(username)) {
            mUsernameText.setError("Username not entered");
            error = true;
        }
        if ("".equals(password)) {
            mPasswordText.setError("Password not entered");
            error = true;
        }
        if (error) {
            return null;
        }
        return obtainCredentialsJSONString(username, password);

    }

    /**
     * Obtain the credentials json object required to log user in on server.
     *
     * @param username username of user.
     * @param password plaintext password of user.
     * @return the credentials json object as a string.
     */
    private String obtainCredentialsJSONString(String username, String password) {
        try {
            return new JSONObject()
                    .put("credentials", new JSONObject()
                            .put("username", username)
                            .put("password", password)
                    ).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }


    @Override
    public void onSuccessResponse(String response) {
        try {
            String token = new JSONObject(response).getString("token");
            // Save token in shared preferences.
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.login_token), token);
            editor.apply();
            Log.d(TAG, "Saved token to shared preferences");
            GlobalFactory.getUserSessionInterface().setUserToken(this, token);
            Log.d(TAG, "username: " + GlobalFactory.getUserSessionInterface().getUsername());
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            Toast.makeText(this, "Error, unable to login, try updating app", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        switch (error.networkResponse.statusCode) {
            case 401:
                showLoginError();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Sorry, Bad Request", Toast.LENGTH_SHORT).show();
        }
        mLoginButton.revertAnimation();
        mLoginButton.setEnabled(true);
    }

    private void showLoginError() {
        String errorMsg = "Username or Password doesn't match";
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        mUsernameText.setError(errorMsg);
        mPasswordText.setError(errorMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String username = extras.getString("username");
                    String password = extras.getString("password");
                    mUsernameText.setText(username);
                    mPasswordText.setText(password);
                    login();
                }
            }
        }
    }

}
