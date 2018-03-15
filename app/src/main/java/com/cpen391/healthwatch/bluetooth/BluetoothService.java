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
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;


public class BluetoothService extends Service {

    public BluetoothService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand (Intent intent, int flags, int startId) {

            Toast.makeText(this, "Bluetooth Service running, please connect to HealthWatch Device", Toast.LENGTH_LONG).show();
            showBtSettings();


            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.
            return START_STICKY;
    }


    private void showBtSettings() {
        Intent openBluetoothSettings = new Intent();
        openBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(openBluetoothSettings);
        setupComms();
    }


    private void setupComms(){

        // Either get the first bonded device, or get bonded device by name, and talk to it
        Set<BluetoothDevice> bondedDevices = BluetoothActivity.mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices){
            if (device.getName().equals("Bose AE2 SoundLink")){
                Log.e("Name", device.getName());

                if (device.ACTION_ACL_CONNECTED.equals("android.bluetooth.device.action.ACL_CONNECTED")){
                    Toast.makeText(this, "HealthWatch has connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}