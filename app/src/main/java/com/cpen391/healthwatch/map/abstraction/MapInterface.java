package com.cpen391.healthwatch.map.abstraction;

import android.content.Context;
import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by william on 2018/3/7.
 * An interface that encapsulates interaction with a map.
 */
public interface MapInterface {
    // Used when map marker is clicked.
    interface OnMarkerClickListener {
        boolean onMarkerClick(MarkerInterface marker);
    }

    interface OnInfoWindowClickListener {
        void onInfoWindowClick(MarkerInterface marker);
    }

    interface OnMapClickListener {
        void onMapClick(LatLng latLng);
    }

    interface OnCameraIdleListener {
        void onCameraIdle();
    }

    /**
     * Sets the style of the map.
     * @param context the application context.
     * @param resId the resource id of the file used for the styling.
     * @return true if successful, false otherwise.
     */
    boolean setMapStyle(Context context, int resId);

    void setMaxZoomPreference(float zoomPreference);

    /**
     * Enable or disable the toolbar on the map.
     * @param enabled true for enable, false for disable.
     */
    void setMapToolbarEnabled(boolean enabled);

    void setOnMarkerClickListener(OnMarkerClickListener clickListener);

    void setOnMapClickListener(OnMapClickListener clickListener);

    /**
     * Get a screen location from a geolocation from the map shown.
     * @param latLng the latitude, longitude on the map.
     * @return a Point on the screen.
     */
    Point projectToScreenLocation(LatLng latLng);

    /**
     * Get a geolocation on the map from a point on the screen.
     * @param point the point on the screen.
     * @return a geolocation on the map shown.
     */
    LatLng projectFromScreenLocation(Point point);

    /**
     * Animate the camera on the map from current location to another location.
     * @param latLng the final location.
     * @param zoomLevel the zoom level to animate to.
     */
    void animateCamera(LatLng latLng, float zoomLevel);

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
     * Sets the callback to be invoked when info window on a marker on the map is clicked.
     * @param listener the callback to be invoked.
     */
    void setOnInfoWindowClickListener(OnInfoWindowClickListener listener);

    /**
     * Enables my location service on the map.
     * @param enabled true to enable, false to disable.
     */
    void setMyLocationEnabled(boolean enabled);

    /**
     * Clear everything on the map.
     */
    void clear();
}
