package com.cpen391.healthwatch.map.marker.animation;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by william on 2018/3/7.
 * An interface for creating marker animations.
 */

public interface AbstractMarkerAnimationFactory {
    /**
     * @param marker the marker to animate.
     * @return an animator for animating a marker entering the map.
     */
    MarkerAnimator createEnterMarkerAnimator(MarkerInterface marker);

    /**
     * @param marker the marker to animate.
     * @return an animator for animating a marker exiting the map.
     */
    MarkerAnimator createExitMarkerAnimator(MarkerInterface marker);

    /**
     * @param marker the marker to animate.
     * @param finalPosition the final position of the marker after animation is complete.
     * @return an animator for animating marker transitioning on the map.
     */
    MarkerAnimator createMarkerTransitionAnimator(MarkerInterface marker, LatLng finalPosition);
}
