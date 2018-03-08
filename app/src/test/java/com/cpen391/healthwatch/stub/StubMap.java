package com.cpen391.healthwatch.stub;

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
    public void setMaxZoomPreference(float zoomPreference) {

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
    public void setMyLocationEnabled(boolean enabled) {

    }
}
