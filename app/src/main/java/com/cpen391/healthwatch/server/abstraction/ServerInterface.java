package com.cpen391.healthwatch.server.abstraction;

import java.util.Map;

/**
 * This interface abstracts the interaction between the client and the server.
 */
public interface ServerInterface {
    /**
     * Sends a string based request message to the server.
     *
     * @param method GET or POST obtained from Request.Method.
     * @param headers headers of the request message.
     * @param body body of the request message.
     * @param callback callback to invoke on success.
     * @param errorCallback error callback to invoke on error.
     */
    void asyncRequest(int method, Map<String,String> headers, String body,
                      ServerCallback callback, ServerErrorCallback errorCallback);
}
