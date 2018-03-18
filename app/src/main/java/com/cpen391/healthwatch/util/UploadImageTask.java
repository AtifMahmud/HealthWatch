package com.cpen391.healthwatch.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.implementation.MultipartRequest.DataPart;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 2018/1/25.
 * Used to decode bitmap of an image and send image to server.
 */
public class UploadImageTask extends AsyncTask<String, Void, Void> {
    private String mDataName;
    private String mPath;
    private List<String> mFilePaths;
    private ServerCallback mCallback;
    private ServerErrorCallback mErrorCallback;

    /**
     * Creates a task to upload images to the server.
     * @param path the path on the server to upload to.
     * @param dataName the name of the multipart data.
     * @param filepaths the file paths of the images to upload on the android device.
     * @param callback the callback to invoke on success.
     * @param errorCallback the callback to invoke on error.
     */
    public UploadImageTask(String path, String dataName, List<String> filepaths,
                           ServerCallback callback, ServerErrorCallback errorCallback) {
        mPath = path;
        mDataName = dataName;
        mFilePaths = filepaths;
        mCallback = callback;
        mErrorCallback = errorCallback;
    }

    @Override
    protected Void doInBackground(String... uris) {
        BitmapFactory.Options options = new Options();
        options.inSampleSize = 4;

        List<DataPart> data = new ArrayList<>();
        for (String filepath : mFilePaths) {
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 70, bos);
            data.add(new DataPart(mDataName, filepath, bos.toByteArray(), "image/jpeg"));
        }
        GlobalFactory.getServerInterface().asyncPost(mPath, data, mCallback, mErrorCallback);
        return null;
    }
}