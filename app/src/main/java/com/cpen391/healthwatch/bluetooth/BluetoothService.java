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
 *      5. https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
 *
 */

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class BluetoothService extends Service {

    private static final String CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";
    private static final String deviceName = "Bose AE2 SoundLink";
    private static final String message = "this is a message for healthwatch";
    private InputStream inputStream;
    private OutputStream outputStream;

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

        BluetoothDevice mBluetoothDevice;

        // Either get the first bonded device, or get bonded device by name, and talk to it
        Set<BluetoothDevice> bondedDevices = BluetoothActivity.mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {

            mBluetoothDevice = device;

            if (mBluetoothDevice.getName().equals(deviceName)) {
                try {
                    talkToDevice(mBluetoothDevice);
                } catch (IOException e){
                    Toast.makeText(this, "IO exception", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    private void talkToDevice(BluetoothDevice device) throws IOException{

        if (device.ACTION_ACL_CONNECTED.equals(CONNECTED)){
            Toast.makeText(this, "HealthWatch is now connected to" + deviceName, Toast.LENGTH_SHORT).show();

            // talk to it
            ParcelUuid[] uuids = device.getUuids();
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());

            try {
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                write(message);

            } catch (IOException e){
                Toast.makeText(this, "IO exception", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please connect to " + deviceName, Toast.LENGTH_SHORT).show();
        }

    }


    public void write(String s) throws IOException {
        Toast.makeText(this, "Writing bytes", Toast.LENGTH_SHORT).show();
        outputStream.write(s.getBytes());
    }


}