package com.cpen391.healthwatch;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.user.LoginActivity;
import com.cpen391.healthwatch.user.SignUpActivity;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by william on 2018/3/16.
 *
 */
@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {
    private LoginActivity mActivity;
    private ServerInterface mMockServer = mock(ServerInterface.class);

    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignUpLink;

    @Before
    public void setUp() throws Exception {
        mActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();
        mUsernameText = mActivity.findViewById(R.id.input_username);
        mPasswordText = mActivity.findViewById(R.id.input_password);
        mLoginButton = mActivity.findViewById(R.id.btn_login);
        mSignUpLink = mActivity.findViewById(R.id.link_sign_up);
        GlobalFactory.setServerInterface(mMockServer);
    }

    @Test
    public void testLoginEmptyFields() {
        mLoginButton.performClick();
        assertNotNull(mUsernameText.getError());
        assertNotNull(mPasswordText.getError());
        verify(mMockServer, times(0)).asyncPost(eq("/gateway/auth/token"), anyMapOf(String.class, String.class), anyString(),
                any(ServerCallback.class), any(ServerErrorCallback.class));
    }

    @Test
    public void testLoginWithCorrectFields() {
        mUsernameText.setText("TestUser");
        mPasswordText.setText("12345678");
        mLoginButton.performClick();
        verify(mMockServer).asyncPost(eq("/gateway/auth/token"), anyMapOf(String.class, String.class), anyString(),
                any(ServerCallback.class), any(ServerErrorCallback.class));
    }

    @Test
    public void testSignUpLink_shouldStartSignUpActivity() {
        mSignUpLink.performClick();
        ShadowActivity shadowActivity = shadowOf(mActivity);
        Intent intent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(intent);
        assertThat(shadowIntent.getIntentClass().getName(), equalTo(SignUpActivity.class.getName()));
    }
}
