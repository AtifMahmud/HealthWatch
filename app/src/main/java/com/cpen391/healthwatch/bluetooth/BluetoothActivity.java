package com.cpen391.healthwatch.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.cpen391.healthwatch.R;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;

public class BluetoothActivity extends AppCompatActivity {

    private final String TAG = BluetoothActivity.class.getSimpleName();
    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final int PAIRED = 3;
    private static final String deviceName = "HealthWatch2";


    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action: " + action);
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                            Toast.makeText(context, "Connected to  " + deviceName, Toast.LENGTH_SHORT).show();
                            startBtService();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mBroadcastReciever, filter);
        checkLocationPermission();

    }


    // Prompts user to enable location permission if not enabled. Otherwise, sets bluetooth up
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        } else {
            setUpBluetooth();
        }
    }


    // Responds to user action on location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_COARSE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpBluetooth();
            } else {
                Toast.makeText(this, "Need permission to use bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Checks if bluetooth enabled
    private void setUpBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Bluetooth not available
        if (mBluetoothAdapter == null){
            Toast.makeText(this, "Sorry, bluetooth not available", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()){
                enableBluetooth();
            }
            else {
                if (checkAvailable()) {
                   startBtService();
                } else {
                    showBtSettings();
                }
            }
        }
    }


    // Enables bluetooth
    private void enableBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
    }


    // Responds to user action on bluetooth permission request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_BLUETOOTH){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Bluetooth connected", Toast.LENGTH_SHORT).show();
               if (checkAvailable()) {
                   startBtService();
               } else {
                   showBtSettings();
               }
            } else {
                Toast.makeText(this, "App needs bluetooth to run", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean checkAvailable(){

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        boolean found = false;

        for (BluetoothDevice device : bondedDevices){
            if(deviceName.equals(device.getName())){
                found = true;
            }
        }

        Log.d(TAG, "Found: " + found);
        return found;
    }


    // Starts bluetooth service
    private void startBtService(){
        Intent bluetoothService = new Intent(this, BluetoothService.class);
        startService(bluetoothService);
    }


    private void showBtSettings() {
        Intent openBluetoothSettings = new Intent();
        openBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(openBluetoothSettings, PAIRED);
        Toast.makeText(this, "Bluetooth Service running, please pair with " + deviceName, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReciever);
        super.onDestroy();
    }


}
