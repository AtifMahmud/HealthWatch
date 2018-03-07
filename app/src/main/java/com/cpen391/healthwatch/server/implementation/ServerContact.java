package com.cpen391.healthwatch.server.implementation;

import com.android.volley.Request.Method;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;

import java.util.Map;

public class ServerContact implements ServerInterface {

    private final AppControlInterface mAppControl;

    public ServerContact(AppControlInterface appControl) {
        mAppControl = appControl;
    }

    @Override
    public void asyncPost(String path, Map<String, String> headers, String body,
                             ServerCallback callback, ServerErrorCallback errorCallback) {
        String url = ServerInterface.BASE_URL + path;
        CustomStringRequest strRequest = new CustomStringRequest(Method.POST, url, callback,
                errorCallback, headers);
        strRequest.setRequestBody(body);
        mAppControl.addToRequestQueue(strRequest);
    }

    @Override
    public void asyncPost(String path, String body, ServerCallback callback) {
        asyncPost(path, null, body, callback, null);
    }

    @Override
    public void asyncGet(String path, Map<String, String> headers,
                         final ServerCallback callback, final ServerErrorCallback errorCallback) {
        String url = ServerInterface.BASE_URL + path;
        CustomStringRequest strRequest = new CustomStringRequest(Method.GET, url, callback,
                errorCallback, headers);
        mAppControl.addToRequestQueue(strRequest);
    }

    @Override
    public void asyncGet(String path, ServerCallback callback) {
        asyncGet(path, null, callback, null);
    }
}
