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

import android.Manifest;
import android.Manifest.permission;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.cpen391.healthwatch.util.GlobalFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MapActivity.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION = 1;
    private final int REQUEST_ENABLE_BLUETOOTH = 2;
    private final int REQUEST_BLUETOOTH_SETTINGS = 3;

    private MapInterface mMap;
    private List<IconMarker> mCurrentIcons;
    private LocationManager mLocationManager;

    private BluetoothAdapter mBluetoothAdapter;
    private String mHealthWatchBTAddress;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action: " + action);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Device: " + device);
                String deviceName = device.getName();
                String matchName = getString(R.string.health_watch);
                if (matchName.equals(deviceName)) {
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.healthwatch_bt_address), device.getAddress());
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Connected to HealthWatch device", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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
        unregisterReceiver(mBroadcastReceiver);
    }

    private void setupBluetooth() {
        // Set up bluetooth ensuring that it's enabled.
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mHealthWatchBTAddress = sharedPref.getString(getString(R.string.healthwatch_bt_address), "");
        if (mHealthWatchBTAddress.isEmpty()) {
            // User never paired with a healthwatch device, we prompt them to connect.
            BluetoothDialog dialog = new BluetoothDialog();
            dialog.setListener(new OnClickDialogListener() {
                @Override
                public void onPositiveClick() {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                attemptToConnectToHealthWatch();
            } else {
                Toast.makeText(this, "Bluetooth is off, Healthwatch will not be paired",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BLUETOOTH_SETTINGS) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getString(getString(R.string.healthwatch_bt_address), "").isEmpty()) {
                Toast.makeText(this, "HealthWatch device was not paired", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Attempts to connect to health watch, asking for permission if required.
     */
    private void attemptToConnectToHealthWatch() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        } else {
            connectToHealthWatch();
        }
    }

    /**
     * Precondition: All permissions are satisfied.
     */
    private void connectToHealthWatch() {
        Log.d(TAG, "Connecting to HealthWatch");
        if (mHealthWatchBTAddress.isEmpty()) {
            // Register the broadcast receiver to discover nearby devices.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            registerReceiver(mBroadcastReceiver, filter);
            Toast.makeText(this, "Please connect to a HealthWatch device", Toast.LENGTH_SHORT).show();
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

    /**
     * Check that bluetooth is enabled, enable it if not enabled, otherwise try connecting to HealthWatch device..
     */
    private void checkBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            attemptToConnectToHealthWatch();
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
        mMap.setOnCameraIdleListener(new OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                checkToUpdateMarkers();
            }
        });

        JSONArray locationsArray = genTestLocations();

        // Parse through the JSON array and show the locations
        for (int i = 0; i < locationsArray.length(); i++) {
            try {
                addMarker(locationsArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Location loc = getLastBestLocation();
        if (loc != null) {
            final int ZOOM_LEVEL = 15;
            mMap.animateCamera(loc.getLatitude(), loc.getLongitude(), ZOOM_LEVEL);
        }
    }

    private void checkToUpdateMarkers() {
        final float MARKER_DISPLAY_ZOOM_LEVEL = 15.0f;
        if (mMap.getCameraZoomLevel() > MARKER_DISPLAY_ZOOM_LEVEL) {
            Log.d(TAG, "Displaying current markers");
            displayCurrentMarkers();
        } else {
            Log.d(TAG, "Hiding current markers");
            hideCurrentMarkers();
        }
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
                    connectToHealthWatch();
                }
            } else {
                // Permission was denied. Display a toast to notify user.
                Toast.makeText(getApplicationContext(), "Location Services Disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Adds a marker onto the map.
     * <p>
     * PreCondition: the map is setup, the input json object contains all the required fields.
     * PostCondition: a marker is added to the map.
     *
     * @param jsonMarker holds information about what the marker is, where it is, etc on the map.
     *                   Must contain the fields: place, type, latitude and longitude.
     */
    private void addMarker(JSONObject jsonMarker) {
        IconMarker iconMarker = IconMarker.makeMarker(mMap, jsonMarker);
        if (iconMarker != null) {
            mCurrentIcons.add(iconMarker);
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
}
