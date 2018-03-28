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
import android.content.IntentSender;
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

import com.cpen391.healthwatch.caretaker.CareTakerActivity;
import com.android.volley.VolleyError;
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
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserSessionInterface;
import com.cpen391.healthwatch.util.Callback;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsRequest.Builder;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MapActivity.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION = 1;
    private final int REQUEST_ENABLE_BLUETOOTH = 2;
    private final int REQUEST_BLUETOOTH_SETTINGS = 3;
    private final int REQUEST_CHECK_LOCATION_SETTINGS = 4;

    private MapInterface mMap;
    private List<IconMarker> mCurrentIcons;

    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

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

    // Timer to periodically pull user locations from the server.
    private Timer mLocationRequestTimer;

    private List<MarkerInterface> mUserMarkers;

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setListeners();
        setupBluetooth();
        createLocationRequest();
        startLocationUpdates();
        setPeriodicLocationPulling();
        mUserMarkers = new ArrayList<>();
    }

    private void setPeriodicLocationPulling() {
        mLocationRequestTimer = new Timer();
        mLocationRequestTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getOtherUserLocations();
            }
        }, 0, 5000);
    }

    /**
     * Get the location of other users and update it on the map.
     */
    private void getOtherUserLocations() {
        Log.d(TAG, "Getting other user location");
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/patients/location", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained response: " + response);
                updateOtherUserLocationsOnMap(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "getting other user location obtained error");
            }
        });
    }

    private void updateOtherUserLocationsOnMap(String response) {
        try {
            JSONArray userLocationArr = new JSONArray(response);
            for (int i = 0; i < userLocationArr.length(); i++) {
                JSONObject userLocationJSON = userLocationArr.getJSONObject(i);
                updateSingleOtherUserOnMap(userLocationJSON);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSingleOtherUserOnMap(JSONObject userLocationJSON) throws JSONException {
        String username = userLocationJSON.getString("username");
        JSONObject locationJSON = userLocationJSON.getJSONObject("location");
        double lat = locationJSON.getDouble("lat");
        double lng = locationJSON.getDouble("lng");
        LatLng position = new LatLng(lat, lng);
        MarkerInterface marker = userInMarkerList(username);
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().title(username).position(position));
            mUserMarkers.add(marker);
        } else {
            MarkerAnimator transitionAnimator = GlobalFactory.getAbstractMarkerAnimationFactory()
                    .createMarkerTransitionAnimator(marker, position);
            transitionAnimator.start();
        }
    }

    private MarkerInterface userInMarkerList(String username) {
        for (MarkerInterface marker : mUserMarkers) {
            if (username.equals(marker.getTitle())) {
                return marker;
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationRequestTimer.cancel();

        stopLocationUpdates();
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
        } else if (requestCode == REQUEST_CHECK_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Location update enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location update disabled", Toast.LENGTH_SHORT).show();
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

    // THIS IS WHERE WE START. SO MODIFY THIS TO GO TO CARETAKER ACTIVITY
    private void setListeners() {
        FloatingActionButton actionButton = findViewById(R.id.btn_profile);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (GlobalFactory.getUserSessionInterface().getUserType()) {
                    case UserSessionInterface.CARETAKER:
                        intent = new Intent(MapActivity.this, CareTakerActivity.class);
                        break;
                    default:
                        intent = new Intent(MapActivity.this, PatientActivity.class);
                }
                startActivity(intent);
            }
        });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "Getting location result: " + locationResult);
                if (locationResult == null) {
                    return;
                }
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    sendLocationToServer(lastLocation);
                }
            }
        };
    }

    /**
     * Sends the location of the user to the server.
     *
     * @param location location of the user, must not be null.
     */
    private void sendLocationToServer(Location location) {
        String jsonLocation = getLocationJSON(location);
        GlobalFactory.getServerInterface()
                .asyncPost("/gateway/user/location", jsonLocation, new ServerCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        Log.i(TAG, "User location updated on server");
                    }
                });
    }

    /**
     * @param location location of the user.
     * @return the json location string required to update user's location on the server.
     */
    private String getLocationJSON(Location location) {
        long time = location.getTime();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        try {
            return new JSONObject()
                    .put("id", GlobalFactory.getUserSessionInterface().getUsername())
                    .put("time", time)
                    .put("lat", lat)
                    .put("lng", lng)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
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
     *
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
     *                       Adds a marker onto the map.
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
            animator.start();
        } else {
            animator = GlobalFactory.getAbstractMarkerAnimationFactory().createExitMarkerAnimator(marker);
            animator.start(new Callback() {
                @Override
                public void callback() {
                    marker.hideInfoWindow();
                }
            });
        }
    }

    /**
     * Create location request required to configure location settings to allow for periodic
     * location updates.
     */
    private void createLocationRequest() {
        Log.d(TAG, "create location request");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. Initialize location request here.
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but we can fix it by showing the
                    // user a dialog.
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapActivity.this, REQUEST_CHECK_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        Log.d(TAG, "Starting location updates");
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        } else {
            Log.i(TAG, "Trying to start location updates, but no permission is granted");
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * @param geoPoint      one of the geo points.
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
