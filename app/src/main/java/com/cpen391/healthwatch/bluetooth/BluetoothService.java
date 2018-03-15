package com.cpen391.healthwatch.bluetooth;

/**
 * Service to handle bluetooth funtionality. We are using a service over activity because we want the functionality to be rather long term.
 *
 * @author: Atif Mahmud
 * @since: 2018-03-03
 *
 * Sources:
 *      1. https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent, int, int)
 *      2. https://developer.android.com/guide/topics/connectivity/bluetooth.html
 *      3. https://www.androidauthority.com/community/threads/trying-to-get-a-list-of-available-bluetooth-devices.25490/  --- ???
 *      4. https://developer.android.com/guide/topics/ui/dialogs.html
 *
 */


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class BluetoothService extends Service {

    private static final int BLUETOOTH_PERMISSION = 1;


    public BluetoothService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

            Toast.makeText(this, "Bluetooth Service running, please connect to HealthWatch Device", Toast.LENGTH_LONG).show();
            showBtSettings();

            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.
            return START_STICKY;
    }


    private void showBtSettings(){
        Intent openBluetoothSettings = new Intent();
        openBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBluetoothSettings);
    }

}