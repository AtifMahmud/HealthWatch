package com.cpen391.healthwatch.bluetooth;

/**
 * Service to handle bluetooth funtionality. We are using a service over activity because we want the functionality to be rather long term.
 *
 * @author: Atif Mahmud]
 * @since: 2018-03-03
 *
 * Sources:
 *      1. https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent, int, int)
 *      2. https://developer.android.com/guide/topics/connectivity/bluetooth.html
 *
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service {
    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if bluetooth adapter exists
        if (mBluetoothAdapter == null){
            // Bluetooth not available
        }

        // If bluetooth is not connected, request permission to turn bluetooth on
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
}