package com.cpen391.healthwatch.map.implementation;

import android.content.Context;
import android.graphics.Point;

import com.cpen391.healthwatch.map.abstraction.MapInterface;
import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william on 2018/3/7.
 *
 */
public class CustomGoogleMap implements MapInterface {
    private Map<Marker, MarkerInterface> mMarkerMapping;
    private GoogleMap mMap;

    public CustomGoogleMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMarkerMapping = new HashMap<>();
    }

    @Override
    public boolean setMapStyle(Context context, int resId) {
        return mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, resId));
    }

    @Override
    public void setMaxZoomPreference(float zoomPreference) {
        mMap.setMaxZoomPreference(zoomPreference);
    }

    @Override
    public void setMapToolbarEnabled(boolean enabled) {
        mMap.getUiSettings().setMapToolbarEnabled(enabled);
    }

    @Override
    public void setOnMarkerClickListener(final OnMarkerClickListener clickListener) {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return clickListener.onMarkerClick(mMarkerMapping.get(marker));
            }
        });
    }

    @Override
    public void setOnMapClickListener(final OnMapClickListener clickListener) {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clickListener.onMapClick(latLng);
            }
        });
    }

    @Override
    public Point projectToScreenLocation(LatLng latLng) {
        return mMap.getProjection().toScreenLocation(latLng);
    }

    @Override
    public LatLng projectFromScreenLocation(Point point) {
        return mMap.getProjection().fromScreenLocation(point);
    }

    @Override
    public void animateCamera(LatLng latLng, float zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoomLevel)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
    public void setOnInfoWindowClickListener(final OnInfoWindowClickListener listener) {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                listener.onInfoWindowClick(mMarkerMapping.get(marker));
            }
        });
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) throws SecurityException {
        mMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void clear() {
        mMap.clear();
    }
}
