package com.cpen391.healthwatch.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cpen391.healthwatch.R;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_COARSE_LOCATION = 2;
    private String TAG = BluetoothActivity.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        checkLocationPermission();
    }

    private void connectBluetooth(){
           mBluetoothAdapter = ((BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

            // Check if bluetooth adapter exists
            if (mBluetoothAdapter == null){
                Toast.makeText(this, "Sorry, bluetooth unavailable", Toast.LENGTH_SHORT).show();
            }

            else {
                // If bluetooth is not connected, request permission to turn bluetooth on
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH);
                }

                if (mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Show Devices", Toast.LENGTH_SHORT).show();
                    Intent openBluetoothSettings = new Intent();
                    openBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(openBluetoothSettings);
                }

            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_BLUETOOTH){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
                openBluetoothSettingsMenu();

            } else {
                Toast.makeText(this, "Bluetooth turned off. Go to settings to turn on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        } else {
            connectBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_COARSE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectBluetooth();
            } else {
                Toast.makeText(this, "Need permission to use bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openBluetoothSettingsMenu(){
        Intent openBluetoothSettings = new Intent();
        openBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBluetoothSettings);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
