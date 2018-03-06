package com.cpen391.healthwatch.server;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;

import java.util.Map;

public class ServerCommunication implements ServerInterface {
    static final String HOST = "ec2api.win";

    @Override
    public void asyncRequest(int method, Map<String, String> headers, String body,
                             ServerCallback callback, ServerErrorCallback errorCallback) {

    }
}
