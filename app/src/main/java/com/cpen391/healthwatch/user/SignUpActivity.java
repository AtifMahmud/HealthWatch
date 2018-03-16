package com.cpen391.healthwatch.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by william on 2018/3/8.
 *
 */
public class SignUpActivity extends Activity
    implements ServerCallback, ServerErrorCallback {
    private final String TAG = SignUpActivity.class.getSimpleName();

    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;
    private CircularProgressButton mSignUpButton;

    private String mUsername;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        obtainViews();
        setListeners();
    }

    private void setListeners() {
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        TextView loginLink = findViewById(R.id.link_login);
        loginLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void obtainViews() {
        mUsernameText = findViewById(R.id.input_username);
        mPasswordText = findViewById(R.id.input_password);
        mConfirmPasswordText = findViewById(R.id.input_password_confirm);
        mSignUpButton = findViewById(R.id.btn_sign_up);
    }

    /**
     * Validates that the user has inputted to every field correctly.
     *
     * @return true if every field is inputted correctly, false otherwise.
     */
    private boolean validate() {
        mUsernameText.setError(null);
        mPasswordText.setError(null);
        mConfirmPasswordText.setError(null);

        boolean noError = true;
        final int MIN_USERNAME_LENGTH = 2;
        final int MIN_PASSWORD_LENGTH = 8;

        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String confirmPassword = mConfirmPasswordText.getText().toString();

        if (username.length() < MIN_USERNAME_LENGTH) {
            mUsernameText.setError(String.format(Locale.CANADA, "Username must be greater than %d letters", MIN_USERNAME_LENGTH));
            noError = false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            mPasswordText.setError(String.format(Locale.CANADA, "Password must be greater than %d characters", MIN_PASSWORD_LENGTH));
            noError = false;
        }

        if (!confirmPassword.equals(password)) {
            mConfirmPasswordText.setError("Passwords do not match");
            noError = false;
        }

        return noError;
    }

    private void signUp() {
        Log.d(TAG, "Sign Up");

        if (validate()) {
            mSignUpButton.setEnabled(false);
            mSignUpButton.startAnimation();

            String body = obtainUserJSONString();
            GlobalFactory.getServerInterface().asyncPost("/gateway/auth/user", null, body, this, this);
        }
    }

    public void onErrorResponse(VolleyError error) {
        switch(error.networkResponse.statusCode) {
            case 400: Toast.makeText(getApplicationContext(), "Bad Request", Toast.LENGTH_SHORT).show();
                break;
            case 403: Toast.makeText(getApplicationContext(), "Username exists", Toast.LENGTH_SHORT).show();
                break;
            default: Toast.makeText(getApplicationContext(), "Cannot Connect", Toast.LENGTH_SHORT).show();
        }
        mSignUpButton.revertAnimation();
        mSignUpButton.setEnabled(true);
    }

    private String obtainUserJSONString() {
        try {
            mUsername = mUsernameText.getText().toString();
            mPassword = mPasswordText.getText().toString();
            return new JSONObject()
            .put("user", new JSONObject()
                    .put("username", mUsername)
                    .put("password", mPassword))
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSignUpButton.dispose();
    }

    @Override
    public void onSuccessResponse(String response) {
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("username", mUsername);
        extras.putString("password", mPassword);
        data.putExtras(extras);
        setResult(RESULT_OK, data);
        finish();
    }
}
