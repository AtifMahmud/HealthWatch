package com.cpen391.healthwatch.map.marker.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.cpen391.healthwatch.util.Callback;

/**
 * Created by william on 2018/3/7.
 * Fades a maker in or out.
 */
public class FadeMarkerAnimator implements MarkerAnimator {
    private ValueAnimator mAnimator;

    FadeMarkerAnimator(final MarkerInterface marker, boolean fadeIn) {
        mAnimator = fadeIn ? ValueAnimator.ofFloat(0, 1) : ValueAnimator.ofFloat(1, 0);
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                marker.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });
    }

    @Override
    public void start() {
        mAnimator.start();
    }

    @Override
    public void start(final Callback callback) {
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                callback.callback();
            }
        });
        mAnimator.start();
    }
}
