package com.cpen391.healthwatch.map;

/**
 * Created by atifm on 3/5/2018.
 *
 * Implements an Item to add as type to ClusterItem
 *
 * Sources:
 *     1. https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 *     2. https://stackoverflow.com/questions/36522305/android-cluster-manager-icon-depending-on-type
 *
 */

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem{

    private String title;
    private String snippet;
    private LatLng latLng;
    private BitmapDescriptor icon;

    public MapItem(LatLng latLng, String title, String snippet, BitmapDescriptor icon) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }


}
