package com.cpen391.healthwatch.stub;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.server.implementation.MultipartRequest.DataPart;

import java.util.List;
import java.util.Map;

/**
 * Created by william on 2018/3/16.
 *
 */

public class StubServerContact implements ServerInterface {
    @Override
    public void asyncPost(String path, Map<String, String> headers, String body, ServerCallback callback, ServerErrorCallback errorCallback) {

    }

    @Override
    public void asyncPost(String path, String body, ServerCallback callback) {

    }

    @Override
    public void asyncPost(String path, Map<String, String> headers, List<DataPart> dataParts, ServerCallback callback, ServerErrorCallback errorCallback) {

    }

    @Override
    public void asyncGet(String path, Map<String, String> headers, ServerCallback callback, ServerErrorCallback errorCallback) {

    }

    @Override
    public void asyncGet(String path, ServerCallback callback) {

    }
}
