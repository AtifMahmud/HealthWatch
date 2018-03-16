package com.cpen391.healthwatch.stub;

import android.content.Context;

import com.cpen391.healthwatch.user.UserSessionInterface;

/**
 * Created by william on 2018/3/16.
 *
 */
public class StubUserSession implements UserSessionInterface {
    private String mToken;

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public String getUserToken() {
        return mToken;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUserToken(Context context, String token) {
        mToken = token;
    }
}
