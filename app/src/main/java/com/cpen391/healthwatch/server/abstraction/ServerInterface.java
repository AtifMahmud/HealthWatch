package com.cpen391.healthwatch.server.abstraction;

import com.cpen391.healthwatch.server.implementation.MultipartRequest.DataPart;

import java.util.List;
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
     * Makes a Post request with form data.
     *
     * @param path the path to make the request.
     * @param headers the http headers.
     * @param dataParts the files to send in the form.
     * @param callback the callback to invoke on success.
     * @param errorCallback the callback to invoke on error.
     */
    void asyncPost(String path, Map<String, String> headers, List<DataPart> dataParts,
                   ServerCallback callback, ServerErrorCallback errorCallback);

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

    /**
     * Makes a POST request to a server by specifying the complete url.
     * @param url the url of the server.
     * @param headers the headers of the post request.
     * @param body the body of the message.
     * @param callback the callback to invoke on success.
     * @param errorCallback the callback to invoke on error.
     */
    void asyncPost2(String url, Map<String, String> headers, String body, ServerCallback callback, ServerErrorCallback errorCallback);
}
