package com.cpen391.healthwatch;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.stub.StubServerContact;
import com.cpen391.healthwatch.stub.StubUserSession;
import com.cpen391.healthwatch.user.LoginActivity;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule = new IntentsTestRule<>(LoginActivity.class);

    private class CustomStubServerContact extends StubServerContact {
        // Emulates a server where all requests are successful.
        @Override
        public void asyncPost(String path, Map<String, String> headers, String body,
                              ServerCallback callback, ServerErrorCallback errorCallback) {
            String response = "";
            try {
                if ("/gateway/auth/token".equals(path)) {
                    response = new JSONObject()
                            .put("token", "random-token-string")
                            .toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callback.onSuccessResponse(response);
        }
    }

    @Before
    public void setUp() {
        GlobalFactory.setServerInterface(new CustomStubServerContact());
        GlobalFactory.setUserSessionInterface(new StubUserSession());
    }

    /**
     * Must disable google auto-fill service for this test, or else we get unable to inject error.
     */
    @Test
    public void loginSuccessful_andHaveToken() throws Exception {
        onView(withId(R.id.input_username)).perform(typeText("testUser"));
        onView(withId(R.id.input_password)).perform(typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        assertNotNull(GlobalFactory.getUserSessionInterface().getUserToken());
    }
}
