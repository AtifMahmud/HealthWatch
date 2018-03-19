package com.cpen391.healthwatch.server.implementation;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by william on 2018/3/6.
 * A class that allows you to make request to the server that could include a string body.
 */
public class CustomStringRequest extends StringRequest {
    private Map<String, String> mHeaders;
    private String mBodyContentType;
    private String mRequestBody;

    CustomStringRequest(int method, String url, final ServerCallback callback,
                        final ServerErrorCallback errorCallback,
                        Map<String,String> headers) {
        super(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callback != null) {
                    callback.onSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorCallback != null) {
                    errorCallback.onErrorResponse(error);
                }
            }
        });
        mHeaders = headers;
        if (method != Method.GET) {
            mBodyContentType = "application/json; charset=utf-8";
        }
    }

    void setRequestBody(String requestBody) {
        mRequestBody = requestBody;
    }

    @Override
    public String getBodyContentType() {
        return mBodyContentType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }
}
