package com.cpen391.healthwatch.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by william on 2018/3/18.
 *
 * Custom NetworkImageView that performs animations when loading the image for Volley's NetworkImageView
 */
public class FadeInNetworkImageView extends NetworkImageView {

    public interface OnLoadCompleteListener {
        void onLoadComplete();
    }

    private static final int FADE_IN_TIME_MS = 250;

    private OnLoadCompleteListener mListener;

    public FadeInNetworkImageView(Context context) {
        super(context);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        mListener = onLoadCompleteListener;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (mListener != null) {
            mListener.onLoadComplete();
        }
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(android.R.color.transparent)),
                new BitmapDrawable(getContext().getResources(), bm)
        });

        setImageDrawable(td);
        td.startTransition(FADE_IN_TIME_MS);
    }
}