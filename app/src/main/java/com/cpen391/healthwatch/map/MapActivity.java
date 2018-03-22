/*
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

import android.Manifest.permission;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.bluetooth.BluetoothDialog;
import com.cpen391.healthwatch.bluetooth.BluetoothDialog.OnClickDialogListener;
import com.cpen391.healthwatch.bluetooth.BluetoothService;
import com.cpen391.healthwatch.map.abstraction.MapInterface;
import com.cpen391.healthwatch.map.abstraction.MapInterface.OnCameraIdleListener;
import com.cpen391.healthwatch.map.abstraction.MarkerInterface;
import com.cpen391.healthwatch.map.implementation.CustomGoogleMap;
import com.cpen391.healthwatch.map.marker.IconMarker;
import com.cpen391.healthwatch.map.marker.animation.MarkerAnimator;
import com.cpen391.healthwatch.patient.PatientActivity;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MapActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MapActivity.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION = 1;
    private final int REQUEST_ENABLE_BLUETOOTH = 2;
    private final int REQUEST_BLUETOOTH_SETTINGS = 3;

    private MapInterface mMap;
    private List<IconMarker> mCurrentIcons;
    private LocationManager mLocationManager;
    // The last center camera point used to determine when to request map
    // icons from server again.
    private LatLng mLastCameraCenter;

    private BluetoothAdapter mBluetoothAdapter;
    private String mHealthWatchBTAddress;


    private void saveBluetoothDevice(BluetoothDevice device) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.healthwatch_bt_address), device.getAddress());
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_icon_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initMapInterface(new CustomGoogleMap(googleMap));
            }
        });
        setListeners();
        setupBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBluetoothService();
    }

    private void setupBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkPairBluetoothDevices();
        // Set up bluetooth ensuring that it's enabled.
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mHealthWatchBTAddress = sharedPref.getString(getString(R.string.healthwatch_bt_address), "");
        if (mHealthWatchBTAddress.isEmpty()) {
            // User never paired with a healthwatch device, we prompt them to connect.
            BluetoothDialog dialog = new BluetoothDialog();
            dialog.setListener(new OnClickDialogListener() {
                @Override
                public void onPositiveClick() {
                    checkBluetooth();
                }
                @Override
                public void onNegativeClick() {
                    Toast.makeText(getApplicationContext(),
                            "App will not connect with HealthWatch without bluetooth", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show(getSupportFragmentManager(), "BluetoothDialog");
        } else {
            // Check to see if bluetooth is connected or not, and start to make connection with health watch.
            checkBluetooth();
        }
    }

    /**
     * Check the list of paired bluetooth devices, trying to find the one for Health Watch and connecting
     * to it.
     */
    private boolean checkPairBluetoothDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (getString(R.string.health_watch).equals(device.getName())) {
                mHealthWatchBTAddress = device.getAddress();
                saveBluetoothDevice(device);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                connectToHealthWatch();
            } else {
                Toast.makeText(this, "Bluetooth is off, Healthwatch will not be paired",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BLUETOOTH_SETTINGS) {
            if (checkPairBluetoothDevices()) {
                Toast.makeText(this, "Bluetooth settings saved", Toast.LENGTH_SHORT).show();
                connectToHealthWatch();
            } else {
                Toast.makeText(this, "Health Watch device not paired", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void connectToHealthWatch() {
        Log.d(TAG, "Connecting to HealthWatch");
        if (mHealthWatchBTAddress.isEmpty()) {
            Toast.makeText(this, "Please pair with a HealthWatch device", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(intent, REQUEST_BLUETOOTH_SETTINGS);
        } else {
            startBluetoothService();
        }
    }

    private void startBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        intent.putExtra(BluetoothService.BLUETOOTH_ADDRESS, mHealthWatchBTAddress);
        startService(intent);
    }

    private void stopBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        stopService(intent);
    }

    /**
     * Check that bluetooth is enabled, enable it if not enabled, otherwise try connecting to HealthWatch device..
     */
    private void checkBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            connectToHealthWatch();
        }
    }

    private void setListeners() {
        FloatingActionButton actionButton = findViewById(R.id.btn_profile);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, PatientActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes stuff on the map. Can be used to inject the map dependency to allow for
     * unit testing.
     *
     * @param mapInterface the map to initialize.
     */
    public void initMapInterface(MapInterface mapInterface) {
        mMap = mapInterface;
        final float MAX_ZOOM_LEVEL = 16.5f;

        mMap.setMaxZoomPreference(MAX_ZOOM_LEVEL);
        enableLocationServices();
        mCurrentIcons = new ArrayList<>();
        mLastCameraCenter = mMap.getCameraLocationCenter();
        mMap.setOnCameraIdleListener(new OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                checkToUpdateMarkers();
            }
        });

        Location loc = getLastBestLocation();
        if (loc != null) {
            final int ZOOM_LEVEL = 15;
            mMap.animateCamera(loc.getLatitude(), loc.getLongitude(), ZOOM_LEVEL);
        }
    }

    private void checkToUpdateMarkers() {
        final float MARKER_DISPLAY_ZOOM_LEVEL = 15.0f;
        LatLng currentCenter = mMap.getCameraLocationCenter();
        if (mMap.getCameraZoomLevel() > MARKER_DISPLAY_ZOOM_LEVEL) {
            final double MAX_DEVIATION_DIST = 500;
            if (distance(currentCenter, mLastCameraCenter) > MAX_DEVIATION_DIST) {
                Log.d(TAG, "Updating markers");
                mLastCameraCenter = currentCenter;
                loadMarkers(currentCenter);
            } else {
                Log.d(TAG, "Displaying current markers");
                displayCurrentMarkers();
            }
        } else {
            Log.d(TAG, "Hiding current markers");
            hideCurrentMarkers();
        }
    }

    /**
     * Updates the map to display only the new markers.
     * @param newIconMarkers the new list of markers to display.
     */
    private void updateMapMarkers(List<IconMarker> newIconMarkers) {
        // For each new icon if it's not in the old icon list then we add it to the map.
        for (IconMarker newIcon : newIconMarkers) {
            if (!iconMarkerInList(newIcon, mCurrentIcons)) {
                mCurrentIcons.add(newIcon);
                newIcon.addMarker(mMap);
                newIcon.getMarker().setAlpha(0);
            }
        }
        // mCurrentIcons actually contains the new icons now.
        // For each old icon if it's not in the new icon list then we remove it from the map.
        Iterator<IconMarker> it = mCurrentIcons.iterator();
        while (it.hasNext()) {
            IconMarker oldIcon = it.next();
            if (!iconMarkerInList(oldIcon, newIconMarkers)) {
                it.remove();
                oldIcon.getMarker().remove();
            }
        }
    }

    private boolean iconMarkerInList(IconMarker matchIconMarker, List<IconMarker> iconMarkers) {
        for (IconMarker iconMarker : iconMarkers) {
            if (iconMarker.getMarkerId().equals(matchIconMarker.getMarkerId())) {
                return true;
            }
        }
        return false;
    }

    private void loadMarkers(final LatLng currentPos) {
        String path = String.format(Locale.CANADA, "/gateway/health-center/%f/%f", currentPos.latitude, currentPos.longitude);
        GlobalFactory.getServerInterface().asyncGet(path, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "loadMarkers, Obtained response: " + response);
                List<IconMarker> newIconMarkers = createMarkersFromResponse(response);
                updateMapMarkers(newIconMarkers);
                displayCurrentMarkers();
            }
        });
    }

    private List<IconMarker> createMarkersFromResponse(String response) {
        List<IconMarker> iconMarkers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                iconMarkers.add(IconMarker.makeMarker(convertIconJSON(jsonArray.getJSONObject(i))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return iconMarkers;
    }

    /**
     * @return the best last known location of the user.
     */
    private Location getLastBestLocation() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }
            long NetLocationTime = 0;
            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }
            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }
        }
        return null;
    }

    private void enableLocationServices() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0].equals(permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission was denied. Display a toast to notify user.
                Toast.makeText(getApplicationContext(), "Location Services Disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Convert the json object received from the server to the json object used to create the marker.
     *
     * @param serverIconJson the icon json object returned by the server.
     * Adds a marker onto the map.
     *
     * @return the json object required to create the marker.
     */
    private JSONObject convertIconJSON(JSONObject serverIconJson) {
        JSONObject iconJSON = new JSONObject();
        try {
            JSONObject locObject = serverIconJson.getJSONObject("loc");
            JSONArray locArr = locObject.getJSONArray("coordinates");
            iconJSON.put("place", serverIconJson.getString("name"))
                    .put("type", "hospital")
                    .put("id", serverIconJson.getString("id"))
                    .put("latitude", locArr.getDouble(1))
                    .put("longitude", locArr.getDouble(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return iconJSON;
    }

    private void hideCurrentMarkers() {
        for (IconMarker iconMarker : mCurrentIcons) {
            MarkerInterface marker = iconMarker.getMarker();
            if (marker.getAlpha() >= 1) {
                setMarkerVisible(marker, false);
            }
        }
    }

    private void displayCurrentMarkers() {
        for (IconMarker iconMarker : mCurrentIcons) {
            MarkerInterface marker = iconMarker.getMarker();
            if (marker.getAlpha() <= 0) {
                setMarkerVisible(marker, true);
            }
        }
    }

    /**
     * Set a marker's visibility by using a transition.
     *
     * @param marker the marker to manipulate.
     * @param fadeIn true to fade marker in, false to fade marker out.
     */
    private void setMarkerVisible(final MarkerInterface marker, boolean fadeIn) {
        MarkerAnimator animator;
        if (fadeIn) {
            animator = GlobalFactory.getAbstractMarkerAnimationFactory().createEnterMarkerAnimator(marker);
        } else {
            animator = GlobalFactory.getAbstractMarkerAnimationFactory().createExitMarkerAnimator(marker);
        }
        animator.start();
    }

    /**
     *
     * @param geoPoint one of the geo points.
     * @param otherGeoPoint the other geo point.
     * @return the distance between two geo points.
     */
    private double distance(LatLng geoPoint, LatLng otherGeoPoint) {
        Location loc = new Location("geoPoint");
        loc.setLatitude(geoPoint.latitude);
        loc.setLongitude(geoPoint.longitude);
        Location otherLoc = new Location("otherGeoPoint");
        otherLoc.setLatitude(otherGeoPoint.latitude);
        otherLoc.setLongitude(otherGeoPoint.longitude);
        return loc.distanceTo(otherLoc);
    }
}
