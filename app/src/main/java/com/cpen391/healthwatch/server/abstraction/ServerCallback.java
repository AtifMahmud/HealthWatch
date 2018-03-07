package com.cpen391.healthwatch.server.abstraction;

/**
 * Created by william on 2018/3/6.
 * This interface contains the callback function to invoke on successful response from server.
 */
public interface ServerCallback {
    void onSuccessResponse(String response);
}
