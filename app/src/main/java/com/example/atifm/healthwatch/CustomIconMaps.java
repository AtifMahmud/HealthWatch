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


package com.example.atifm.healthwatch;

import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomIconMaps  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    public CustomIconMaps() throws JSONException {
    }

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

        // Dummy JSON for testing purposes
        JSONObject location1 = new JSONObject();
        JSONObject location2 = new JSONObject();
        JSONObject location3 = new JSONObject();
        JSONArray locationsArray = new JSONArray();


//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        try {
            location1.put("type", "ambulance");
            location1.put("latitude", "-34");
            location1.put("longitude", "151");
            location1.put("place", "Sydney");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            location2.put("type", "hospital");
            location2.put("latitude", "23");
            location2.put("longitude", "90");
            location2.put("place", "Dhaka");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            location3.put("type", "ambulance");
            location3.put("latitude", "49");
            location3.put("longitude", "-123");
            location3.put("place", "Vancouver");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        locationsArray.put(location1);
        locationsArray.put(location2);
        locationsArray.put(location3);


        // Parse through the JSON array and show the locations
        for (int i = 0; i < locationsArray.length(); i++) {
            try {
                JSONObject current = locationsArray.getJSONObject(i);
                Double lat = current.getDouble("latitude");
                Double lng = current.getDouble("longitude");
                String loc = current.getString("place");
                String type = current.getString("type");

                LatLng location = new LatLng(lat, lng);

                MarkerOptions marker = (new MarkerOptions().position(location).title(type + " in " + loc));

                // Consider updating to a hashtable to make it scalable?
                if (type.equals("ambulance")){
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_icon));
                }

                // Hospital
                else{
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_icon));
                }

                mMap.addMarker(marker);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

    }

}
