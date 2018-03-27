package com.cpen391.healthwatch.user;

import android.content.Context;

/**
 * Created by william on 2018/1/2.
 * Keeps track of any session state of the app, for instance user logged in state, etc.
 */
public interface UserSessionInterface {
    /**
     *
     * @return true if there is a user is logged in, false otherwise.
     */
    boolean isLoggedIn();

    /**
     *
     * @return token of user used for authentication.
     */
    String getUserToken();

    /**
     * Precondition: an JWT token must be passed into the session with the
     * setUserToken method.
     *
     * @return username of user, or null if no user set.
     */
    String getUsername();

    /**
     *
     * @return the type of the user.
     */
    String getUserType();

    /**
     * Sets jwt user token.
     *
     * Obtains the username from the "sub" field of the jwt token and
     * sets it as the internal username of this user session.
     *
     * @param context the context used to read the public key from resources.
     * @param token the jwt token.
     */
    void setUserToken(Context context, String token);
}