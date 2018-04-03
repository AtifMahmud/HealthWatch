package com.cpen391.healthwatch.map.abstraction;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by william on 2018/3/7.
 * An interface for manipulating markers on a map interface.
 */
public interface MarkerInterface {
    /**
     * Get the geolocation of the marker.
     * @return geolocation of marker.
     */
    LatLng getPosition();

    /**
     * Sets the alpha value of the marker, making it more of less opaque.
     * @param alpha alpha value to set.
     */
    void setAlpha(float alpha);

    /**
     *
     * @return alpha value of marker.
     */
    float getAlpha();

    /**
     * Remove the marker from the map.
     */
    void remove();

    /**
     * Sets the position of the marker on the map.
     * @param latLng geolocation to the marker to.
     */
    void setPosition(LatLng latLng);

    /**
     * Sets the title of the marker.
     * @param title title of marker.
     */
    void setTitle(String title);

    /**
     * Sets the snippet of the marker, which is shown in the marker's info window.
     * @param snippet snippet of the marker to be shown.
     */
    void setSnippet(String snippet);

    /**
     * Shows the info window associated with the marker.
     */
    void showInfoWindow();

    /**
     * Hides the info window associated with the marker.
     */
    void hideInfoWindow();

    /**
     * Sets the icon of the marker.
     * @param resId resource id of the image to set marker to.
     */
    void setIcon(int resId);

    /**
     * Gets the title of the marker.
     */
    String getTitle();
}
