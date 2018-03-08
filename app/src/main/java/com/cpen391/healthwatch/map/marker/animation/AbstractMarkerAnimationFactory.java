package com.cpen391.healthwatch.map.marker.animation;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;

/**
 * Created by william on 2018/3/7.
 * An interface for creating marker animations.
 */

public interface AbstractMarkerAnimationFactory {
    /**
     * Creates an animator to return an animation for a marker entering the map.
     * @return a marker animator.
     */
    MarkerAnimator createEnterMarkerAnimator(MarkerInterface marker);

    /**
     * Creates an animator to return an animation for a marker exiting the map.
     * @return a marker animator.
     */
    MarkerAnimator createExitMarkerAnimator(MarkerInterface marker);
}
