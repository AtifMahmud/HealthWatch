package com.cpen391.healthwatch.server.abstraction;

/**
 * Created by william on 2018/3/6.
 * This interface contains the error callback function to invoke on error response received from server.
 */
public interface ServerErrorCallback {
    void onErrorResponse(String errorMessage);
}
