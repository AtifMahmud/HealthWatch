package com.cpen391.healthwatch.server.implementation;

import com.android.volley.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by william on 2018/1/25.
 * A class that allows you to send binary files to a http server using volley.
 *
 * Adapted from github: https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
 */
public class MultipartRequest extends Request<String> {
    private final String TWO_HYPHENS = "--";
    private final String CRLF = "\r\n";
    private final String BOUNDARY = "healthwatch-" + System.currentTimeMillis();

    private Map<String, String> mHeaders;
    private List<DataPart> mData;
    private Response.Listener<String> mListener;
    private ServerErrorCallback mErrorListener;

    /**
     *
     * @param url url to send multipart request to.
     * @param headers the headers of the request.
     * @param callback the callback to invoke on success.
     * @param errorCallback the error callback to invoke on error.
     */
    MultipartRequest(String url, Map<String, String> headers, List<DataPart> data,
                     final ServerCallback callback,
                     final ServerErrorCallback errorCallback) {
        super(Method.POST, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorCallback != null) {
                    errorCallback.onErrorResponse(error);
                }
            }
        });
        mListener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callback != null) {
                    callback.onSuccessResponse(response);
                }
            }
        };
        mErrorListener = errorCallback;
        mHeaders = headers;
        mData = data;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // Populate data byte payload.
            if (mData != null && mData.size() > 0) {
                dataParse(dos, mData);
            }
            dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF);
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers)),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    /**
     * Parse data into data output stream.
     * @param dos data output stream to feed data into.
     * @param data the data files to feed to output stream.
     * @throws IOException on I/O error.
     */
    private void dataParse(DataOutputStream dos, List<DataPart> data) throws IOException {
        for (DataPart dataPart : data) {
            buildDataPart(dos, dataPart, dataPart.getName());
        }
    }

    /**
     * Write data part into data output stream, along with headers required for each multipart data.
     * @param dos the data output stream to write to.
     * @param dataFile the data file containing the data we want to write.
     * @param inputName the name of the data input to include in the multipart header.
     * @throws IOException on I/O error.
     */
    private void buildDataPart(DataOutputStream dos, DataPart dataFile, String inputName) throws IOException {
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + CRLF);
        dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s",
                inputName, dataFile.getFileName(), CRLF));
        String contentType = dataFile.getType();
        if (contentType != null && !contentType.trim().isEmpty()) {
            dos.writeBytes("Content-Type: " + contentType + CRLF);
        }
        dos.writeBytes(CRLF);
        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while(bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dos.writeBytes(CRLF);
    }

    public static class DataPart {
        private String mName;
        private String mFileName;
        private byte[] mContent;
        private String mType;

        /**
         * @param name the name of the data part.
         * @param filename file name for the data part.
         * @param data byte data of the file.
         * @param contentType Content-Type, e.g "image/jpeg"
         */
        public DataPart(String name, String filename, byte[] data, String contentType) {
            mName = name;
            mFileName = filename;
            mContent = data;
            mType = contentType;
        }

        String getName() {
            return mName;
        }

        String getFileName() {
            return mFileName;
        }

        byte[] getContent() {
            return mContent;
        }

        String getType() {
            return mType;
        }
    }
}