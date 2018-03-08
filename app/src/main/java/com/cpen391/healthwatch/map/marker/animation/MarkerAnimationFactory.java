package com.cpen391.healthwatch.map.marker.animation;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;

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
}
