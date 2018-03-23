package com.cpen391.healthwatch.map.marker.animation;

import com.cpen391.healthwatch.util.Callback;

/**
 * Created by william on 2018/3/7.
 * An animator for markers.
 */
public interface MarkerAnimator {
    /**
     * Starts the animation.
     */
    void start();

    /**
     * Optional:
     * Starts the animation passing in a callback to be invoked on animation end.
     * @param callback the callback to be invoked on animation completion.
     */
    void start(Callback callback);
}
