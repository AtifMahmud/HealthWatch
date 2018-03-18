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
 */

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class BluetoothService extends Service {
    private final String TAG = BluetoothService.class.getSimpleName();
    private static final String CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";
    private static final String DEVICE_NAME = "HealthWatch2";
    private static final String MESSAGE = "HELLO";

    private InputStream inputStream;
    private OutputStream outputStream;

    BluetoothDevice mBluetoothDevice;
    Set<BluetoothDevice> bondedDevices;


    public BluetoothService() {

    }

    @Override
    public void onCreate(){
        bondedDevices = BluetoothActivity.mBluetoothAdapter.getBondedDevices();
        setupComms();
        // Either get the first bonded device, or get bonded device by name, and talk to it
        //Toast.makeText(this, "In BTservice, oncreate", Toast.LENGTH_SHORT).show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
       // Toast.makeText(this, "In BTservice, onstartcommand", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }


    private void setupComms(){
        Toast.makeText(this,"Trying to communicate", Toast.LENGTH_SHORT).show();
        for (BluetoothDevice device : bondedDevices) {
            Toast.makeText(this, device.getName(), Toast.LENGTH_SHORT).show();
            mBluetoothDevice = device;

            if (mBluetoothDevice.getName().equals(DEVICE_NAME)) {
                Log.d(TAG, "DeviceName: " + mBluetoothDevice.getName());
                Toast.makeText(this, mBluetoothDevice.getAddress(), Toast.LENGTH_SHORT).show();
                try {
                    talkToDevice(mBluetoothDevice);
                } catch (IOException e){
                    Log.d(TAG, "IOException in setupCommns");
                }

            }
        }
    }


    private void talkToDevice(BluetoothDevice device) throws IOException{
            Toast.makeText(this, "In talkToDevice", Toast.LENGTH_SHORT).show();
            // talk to it
            ParcelUuid[] uuids = device.getUuids();
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());

            try {
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                write(MESSAGE);

            } catch (IOException e){
                Log.d(TAG, "IOException in talkToDevice");
            }

    }


    public void write(String s) throws IOException {
        Toast.makeText(this, "Writing bytes", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "writing to bluetooth");
        outputStream.write(s.getBytes());
    }


}