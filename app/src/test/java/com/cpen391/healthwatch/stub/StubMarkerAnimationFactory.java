package com.cpen391.healthwatch.stub;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.cpen391.healthwatch.map.marker.animation.AbstractMarkerAnimationFactory;
import com.cpen391.healthwatch.map.marker.animation.MarkerAnimator;

/**
 * Created by william on 2018/3/7.
 *
 */

public class StubMarkerAnimationFactory implements AbstractMarkerAnimationFactory {

    private class StubMarkerAnimation implements MarkerAnimator {
        @Override
        public void start() {

        }
    }

    @Override
    public MarkerAnimator createEnterMarkerAnimator(MarkerInterface marker) {
        return new StubMarkerAnimation();
    }

    @Override
    public MarkerAnimator createExitMarkerAnimator(MarkerInterface marker) {
        return new StubMarkerAnimation();
    }
}
