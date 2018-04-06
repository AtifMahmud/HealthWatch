package com.cpen391.healthwatch.map.abstraction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by william on 2018/3/7.
 * An interface that encapsulates interaction with a map.
 */
public interface MapInterface {
    interface OnCameraIdleListener {
        void onCameraIdle();
    }

    void setMaxZoomPreference(float zoomPreference);

    /**
     * Adds a marker to the map.
     * @param options the options for creating the marker.
     * @return the newly created marker.
     */
    MarkerInterface addMarker(MarkerOptions options);

    /**
     * Gets the center location of the map that is currently being displayed.
     * @return geolocation of the center of the map.
     */
    LatLng getCameraLocationCenter();

    /**
     * Gets the camera zoom level of the map.
     * @return zoom level.
     */
    float getCameraZoomLevel();

    /**
     * Sets the callback to be invoked when camera on the map is idle.
     * @param listener the callback to be invoked.
     */
    void setOnCameraIdleListener(OnCameraIdleListener listener);

    /**
     * Enables my location service on the map.
     * @param enabled true to enable, false to disable.
     */
    void setMyLocationEnabled(boolean enabled);

    /**
     * Animate camera to a given latitude and longitude on the map.
     * @param lat latitude.
     * @param lng longitude.
     * @param zoom the level of the zoom, a value of 15 shows roads, higher means zoom out further.
     */
    void animateCamera(double lat, double lng, float zoom);

}
