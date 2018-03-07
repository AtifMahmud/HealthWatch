package com.cpen391.healthwatch.stub;

import android.content.Context;
import android.graphics.Point;

import com.cpen391.healthwatch.map.abstraction.MapInterface;
import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by william on 2018/3/7.
 *
 */
public class StubMap implements MapInterface {
    @Override
    public boolean setMapStyle(Context context, int resId) {
        return false;
    }

    @Override
    public void setMaxZoomPreference(float zoomPreference) {

    }

    @Override
    public void setMapToolbarEnabled(boolean enabled) {

    }

    @Override
    public void setOnMarkerClickListener(OnMarkerClickListener clickListener) {

    }

    @Override
    public void setOnMapClickListener(OnMapClickListener clickListener) {

    }

    @Override
    public Point projectToScreenLocation(LatLng latLng) {
        return null;
    }

    @Override
    public LatLng projectFromScreenLocation(Point point) {
        return null;
    }

    @Override
    public void animateCamera(LatLng latLng, float zoomLevel) {

    }

    @Override
    public MarkerInterface addMarker(MarkerOptions options) {
        return null;
    }

    @Override
    public LatLng getCameraLocationCenter() {
        return null;
    }

    @Override
    public float getCameraZoomLevel() {
        return 0;
    }

    @Override
    public void setOnCameraIdleListener(OnCameraIdleListener listener) {

    }

    @Override
    public void setOnInfoWindowClickListener(OnInfoWindowClickListener listener) {

    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {

    }

    @Override
    public void clear() {

    }
}
