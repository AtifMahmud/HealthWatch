package com.cpen391.healthwatch.map.marker.animation;


import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.cpen391.healthwatch.map.marker.animation.LatLngInterpolator.Linear;
import com.cpen391.healthwatch.util.Callback;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by william on 2018/3/7.
 *
 */
public class MarkerAnimationFactory implements AbstractMarkerAnimationFactory {
    @Override
    public MarkerAnimator createEnterMarkerAnimator(MarkerInterface marker) {
        return new FadeMarkerAnimator(marker, true);
    }

    @Override
    public MarkerAnimator createExitMarkerAnimator(MarkerInterface marker) {
        return new FadeMarkerAnimator(marker, false);
    }

    @Override
    public MarkerAnimator createMarkerTransitionAnimator(final MarkerInterface marker, final LatLng finalPosition) {
        return new MarkerAnimator() {
            @Override
            public void start() {
                markerTranslateAnimation(marker, finalPosition);
            }
            @Override
            public void start(Callback callback) {
                // Not implemented.
            }
        };
    }

    /* Copyright 2013 Google Inc.
        Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */
    private void markerTranslateAnimation(final MarkerInterface marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final LatLngInterpolator latLngInterpolator = new Linear();

        handler.post(new Runnable() {
            long elapsed;
            float t, v;
            @Override
            public void run() {
                // Calculate progress using interpolator.
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
