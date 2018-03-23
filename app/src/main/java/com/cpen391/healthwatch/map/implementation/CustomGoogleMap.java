package com.cpen391.healthwatch.map.implementation;

import com.cpen391.healthwatch.map.abstraction.MapInterface;
import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william on 2018/3/7.
 *
 */
public class CustomGoogleMap implements MapInterface {
    private String TAG = CustomGoogleMap.class.getSimpleName();
    private Map<Marker, MarkerInterface> mMarkerMapping;
    private GoogleMap mMap;

    public CustomGoogleMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMarkerMapping = new HashMap<>();

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // This prevents user from clicking on marker if it is invisible.
                return marker.getAlpha() <= 0;
            }
        });
    }

    @Override
    public void setMaxZoomPreference(float zoomPreference) {
        mMap.setMaxZoomPreference(zoomPreference);
    }

    @Override
    public MarkerInterface addMarker(MarkerOptions markerOptions) {
        Marker actualMarker = mMap.addMarker(markerOptions);
        GoogleMapMarker marker = new GoogleMapMarker(actualMarker);
        mMarkerMapping.put(actualMarker, marker);
        return marker;
    }

    @Override
    public LatLng getCameraLocationCenter() {
        return mMap.getCameraPosition().target;
    }

    @Override
    public float getCameraZoomLevel() {
        return mMap.getCameraPosition().zoom;
    }

    @Override
    public void setOnCameraIdleListener(final OnCameraIdleListener listener) {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                listener.onCameraIdle();
            }
        });
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) throws SecurityException {
        mMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void animateCamera(double lat, double lng, float zoom) {
        LatLng coordinate = new LatLng(lat, lng);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(coordinate, zoom);
        mMap.animateCamera(location);
    }
}
