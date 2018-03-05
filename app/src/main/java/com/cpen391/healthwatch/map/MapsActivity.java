/**
 * This activity will show the locations on the map with custom icons. Before the server is set-up, test
 * with dummy JSON.
 *
 * @author: Atif Mahmud
 * @since: 2018-03-03
 *
 * Sources:
 *      1. https://stackoverflow.com/questions/17810044/android-create-json-array-and-json-object
 *      2. https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng
 *      3. https://stackoverflow.com/questions/28736419/parsing-json-array-and-object-in-android
 *      4. https://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android
 *      5. https://stackoverflow.com/questions/18486503/android-google-maps-api-v2-how-to-change-marker-icon
 *      6. https://stackoverflow.com/questions/29983733/adding-new-markers-on-map-using-cluster-manager-doesnt-reflect-the-changes-unti
 *
 * FOR REFERENCE:
 *       {
 *           "type": String (either "hospital" or "ambulance")
 *           "latitude": int    // North
 *           "longitude":int    // East
 *           "place":  String
 *       }
 *
 */


package com.cpen391.healthwatch.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.atifm.healthwatch.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_icon_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        JSONArray locationsArray = genTestLocations();

        // Parse through the JSON array and show the locations
        for (int i = 0; i < locationsArray.length(); i++) {
            try {
                addMarker(locationsArray.getJSONObject(i));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a marker onto the map.
     *
     * PreCondition: the map is setup, the input json object contains all the required fields.
     * PostCondition: a marker is added to the map.
     *
     * @param jsonMarker holds information about what the marker is, where it is, etc on the map.
     *                   Must contain the fields: place, type, latitude and longitude.
     */
    private void addMarker(JSONObject jsonMarker) {
        ClusterManager<MapItem> mClusterManager = new ClusterManager<MapItem>(this, getMap());

        //getMap().setOnCameraIdleListener((GoogleMap.OnCameraIdleListener) mClusterManager);
        //getMap().setOnMarkerClickListener(mClusterManager);

        try {
            String loc = jsonMarker.getString("place");
            String type = jsonMarker.getString("type");

            LatLng location = new LatLng(jsonMarker.getDouble("latitude"), jsonMarker.getDouble("longitude"));
            MarkerOptions markerOptions = (new MarkerOptions().position(location).title(type + " in " + loc).icon(CustomIcons.getIcon(type)));
            MapItem mItem = new MapItem(location, "foo", "bar", CustomIcons.getIcon(type));
            mClusterManager.addItem(mItem);
            mClusterManager.cluster();

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * Generates some locations for testing.
     */
    private JSONArray genTestLocations() {
        JSONArray locationsArray = new JSONArray();

        locationsArray.put(genStubJsonLocation("ambulance", "-34", "151", "Sydney"));
        locationsArray.put(genStubJsonLocation("hospital", "23", "90", "Dhaka"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.2613790", "-123.2570520", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.263790", "-123.2520", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.26790", "-123.2570520", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.2690", "-123.20520", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.26130", "-123.220", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.2613790", "-123.20", "Vancouver"));
        locationsArray.put(genStubJsonLocation("ambulance", "49.2613790", "-123.2570520", "Vancouver"));

        return locationsArray;
    }

    private JSONObject genStubJsonLocation(String type, String lat, String lng, String place) {
        try {
            return new JSONObject()
                    .put("type", type)
                    .put("latitude", lat)
                    .put("longitude", lng)
                    .put("place", place);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GoogleMap getMap() {
        return mMap;
    }
}
