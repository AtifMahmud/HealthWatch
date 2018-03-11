package com.cpen391.healthwatch;

import android.widget.Button;
import android.widget.EditText;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.user.SignUpActivity;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by william on 2018/3/9.
 *
 */
@RunWith(RobolectricTestRunner.class)
public class SignUpActivityTest {
    private SignUpActivity activity;
    private ServerInterface mockServer = mock(ServerInterface.class);

    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;
    private Button mSignUpButton;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(SignUpActivity.class)
                .create()
                .start()
                .resume()
                .get();
        mUsernameText = activity.findViewById(R.id.input_username);
        mPasswordText = activity.findViewById(R.id.input_password);
        mConfirmPasswordText = activity.findViewById(R.id.input_password_confirm);
        mSignUpButton = activity.findViewById(R.id.btn_sign_up);
        GlobalFactory.setServerInterface(mockServer);
    }

    @Test
    public void testSignUpEmptyFields() {
        mSignUpButton.performClick();
        assertNotNull(mUsernameText.getError());
        assertNotNull(mPasswordText.getError());
    }

    @Test
    public void testNotSamePassword() {
        // Assert error if password and confirm password is not the same.
        String testPassword = "12345678";
        String wrongPassword = "87654321";
        mPasswordText.setText(testPassword);
        mConfirmPasswordText.setText(wrongPassword);
        mSignUpButton.performClick();
        assertNotNull(mConfirmPasswordText.getError());
    }

    @Test
    public void testCorrectSignUp() {
        String username = "testUser";
        String password = "12345678";
        mUsernameText.setText(username);
        mPasswordText.setText(password);
        mConfirmPasswordText.setText(password);
        mSignUpButton.performClick();
        verify(mockServer).asyncPost(eq("/gateway/auth/user"), anyMapOf(String.class, String.class), anyString(),
                any(ServerCallback.class), any(ServerErrorCallback.class));
    }
}
