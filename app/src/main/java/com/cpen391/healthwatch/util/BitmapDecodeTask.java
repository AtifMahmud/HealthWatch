package com.cpen391.healthwatch.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by william on 2018/1/1.
 * Used to decode bitmap off the ui thread.
 */

public class BitmapDecodeTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> mImageViewReference;

    public BitmapDecodeTask(ImageView imageView) {
        mImageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... uris) {
        String uri = uris[0];
        return BitmapFactory.decodeFile(uri);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}