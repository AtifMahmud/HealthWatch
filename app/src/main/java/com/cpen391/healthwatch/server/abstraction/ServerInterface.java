package com.cpen391.healthwatch.server.abstraction;

import java.util.Locale;
import java.util.Map;

/**
 * This interface abstracts the interaction between the client and the server.
 */
public interface ServerInterface {
    String PROTO = "https";
    String HOST = "ec2api.win";
    int PORT = 8080;
    String BASE_URL = String.format(Locale.CANADA,"%s://%s:%d", PROTO, HOST, PORT);

    /**
     * Makes a POST request with a body.
     *
     * @param path the path to make the request.
     * @param headers headers of the request message.
     * @param body body of the request message.
     * @param callback callback to invoke on success.
     * @param errorCallback error callback to invoke on error.
     */
    void asyncPost(String path, Map<String,String> headers, String body,
                      ServerCallback callback, ServerErrorCallback errorCallback);

    /**
     * Makes a Post request with a body.
     *
     * @param path the path to make the request.
     * @param body body of the post request.
     * @param callback callback to invoke on success.
     */
    void asyncPost(String path, String body, ServerCallback callback);

    /**
     * Makes a GET request.
     *
     * @param path the path to make the request.
     * @param headers headers of the request message.
     * @param callback callback to invoke on success.
     * @param errorCallback error callback to invoke on error.
     */
    void asyncGet(String path, Map<String, String> headers, ServerCallback callback,
                  ServerErrorCallback errorCallback);

    /**
     * Makes a GET request.
     *
     * @param path the path to make the request.
     * @param callback callback to invoke on success.
     */
    void asyncGet(String path, ServerCallback callback);
}
