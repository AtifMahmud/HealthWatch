package com.cpen391.healthwatch.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Created by william on 2018/1/1.
 * Used to decode bitmap off the ui thread.
 */

public class BitmapDecodeTask extends AsyncTask<String, Void, Bitmap> {
    public interface ImageDecodeCallback {
        void callback(Bitmap bitmap);
    }
    private ImageDecodeCallback mCallback;

    public BitmapDecodeTask(ImageDecodeCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Bitmap doInBackground(String... uris) {
        String uri = uris[0];
        return BitmapFactory.decodeFile(uri);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            mCallback.callback(bitmap);
        }
    }
}