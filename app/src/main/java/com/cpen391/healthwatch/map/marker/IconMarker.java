package com.cpen391.healthwatch.map.marker;

import com.cpen391.healthwatch.map.abstraction.MapInterface;
import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by william on 2018/3/7.
 * A class that allows for displaying markers with custom icons on a map interface.
 */
public class IconMarker {
    private final LatLng mPosition;
    private String mMarkerId;
    private String mType;
    private String mPlace;
    private MarkerInterface mMarker;

    private IconMarker(String markerId, LatLng position, String type, String place) {
        mMarkerId = markerId;
        mPosition = position;
        mType = type;
        mPlace = place;
    }

    /**
     * Makes an IconMarker.
     *
     * expect json: |
     *  {"place": "string", "type": "string", "id": "string", "latitude": "double", "longitude": "double"}
     *
     * @param jsonMarker the json object that represents the marker.
     * @return the newly created IconMarker or null if the jsonMarker does not properly
     *  represent an icon marker.
     */
    public static IconMarker makeMarker(JSONObject jsonMarker) {
        try {
            String loc = jsonMarker.getString("place");
            String type = jsonMarker.getString("type");
            String markerId = "";
            if (jsonMarker.has("id")) {
                markerId = jsonMarker.getString("id");
            }
            LatLng location = new LatLng(jsonMarker.getDouble("latitude"), jsonMarker.getDouble("longitude"));
            return new IconMarker(markerId, location, type, loc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public String getMarkerId() {
        return mMarkerId;
    }

    public String getType() {
        return mType;
    }

    /**
     * Add marker to map.
     * @param map the map to place marker on.
     */
    public void addMarker(MapInterface map) {
        MarkerOptions options = new MarkerOptions()
                .position(mPosition)
                .title(mPlace)
                .icon(CustomIcons.getIcon(mType));
        mMarker = map.addMarker(options);
    }

    /**
     *
     * Precondition: addMarker must be called before
     *   so that marker is added to the map.
     *
     * @return the marker associated with this object.
     */
    public MarkerInterface getMarker() {
        return mMarker;
    }
}
