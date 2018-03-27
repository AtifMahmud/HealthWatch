package com.cpen391.healthwatch.map.implementation;

import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by william on 2018/3/7.
 *
 */
public class GoogleMapMarker implements MarkerInterface {
    private Marker mMarker;

    GoogleMapMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public LatLng getPosition() {
        return mMarker.getPosition();
    }

    @Override
    public void setAlpha(float alpha) {
        mMarker.setAlpha(alpha);
    }

    @Override
    public float getAlpha() {
        return mMarker.getAlpha();
    }

    @Override
    public void remove() {
        mMarker.remove();
    }

    @Override
    public void setPosition(LatLng latLng) {
        mMarker.setPosition(latLng);
    }

    @Override
    public void setTitle(String title) {
        mMarker.setTitle(title);
    }

    @Override
    public void setSnippet(String snippet) {
        mMarker.setSnippet(snippet);
    }

    @Override
    public void showInfoWindow() {
        mMarker.showInfoWindow();
    }

    @Override
    public void hideInfoWindow() {
        mMarker.hideInfoWindow();
    }

    @Override
    public void setIcon(int resId) {
        mMarker.setIcon(BitmapDescriptorFactory.fromResource(resId));
    }

    @Override
    public String getTitle() {
        return mMarker.getTitle();
    }
}
