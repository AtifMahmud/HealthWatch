package com.cpen391.healthwatch.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import kotlin.text.Charsets;

/**
 * References: google's Bluetooth Chat example.
 */
public class BluetoothService extends Service {

    public interface OnBluetoothDataListener {
        void onDataReceived(String data);
    }
    public static final String BLUETOOTH_ADDRESS = "BT_ADDR";

    private final String TAG = BluetoothService.class.getSimpleName();

    // UUID for connecting to RN-42 bluetooth dongle.
    private static final UUID RN_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private String mHealthWatchAddress;
    private OnBluetoothDataListener mListener;

    private boolean mEndingService;

    private final Binder mBinder = new BluetoothBinder();

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHealthWatchAddress = intent.getStringExtra(BLUETOOTH_ADDRESS);
        Log.d(TAG, "Starting bluetooth service with address: " + mHealthWatchAddress);
        if (mHealthWatchAddress == null) {
            Log.d(TAG, "Error passed in null address to bluetooth service");
            stopSelf();
        } else {
            start();
        }
        return START_STICKY;
    }

    private void onConnected(BluetoothSocket socket) {
        Log.d(TAG, "Started connected thread");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    public synchronized void start() {
        Log.d(TAG, "Started connect thread");
        mConnectThread = new ConnectThread(mHealthWatchAddress);
        mConnectThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    public synchronized void stop() {
        mEndingService = true;
        Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    public synchronized void setOnDataReceiveListener(OnBluetoothDataListener listener) {
        mListener = listener;
    }

    public synchronized void sendReceivedData(String data) {
        if (mListener != null) {
            mListener.onDataReceived(data);
        }
    }

    private void onCannotConnect() {
        Toast.makeText(getApplicationContext(), "Cannot connect to healthwatch device", Toast.LENGTH_SHORT).show();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private synchronized void onConnectionLost() {
        // Send a failure message back to the Activity
        Log.d(TAG, "Connection lost");
        if (!mEndingService) {
            // Start the service over to restart listening mode
            start();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream is = null;
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "input and output stream might not be created");
            }
            mmInputStream = is;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            BufferedReader br = new BufferedReader(new InputStreamReader(mmInputStream, Charsets.US_ASCII));

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    //bytes = mmInputStream.read(buffer);
                    String data = br.readLine();
                    sendReceivedData(data);
                    // Send the obtained bytes to the UI Activity
                    Log.d(TAG,"obtained: " + data);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    onConnectionLost();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread attempts to connect to a bluetooth device.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(String macAddr) {
            mmDevice = mAdapter.getRemoteDevice(macAddr);
            BluetoothSocket socket = null;
            try {
                socket = mmDevice.createInsecureRfcommSocketToServiceRecord(RN_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = socket;
        }

        @Override
        public void run() {
            try {
                mmSocket.connect();
                onConnected(mmSocket);
                synchronized (BluetoothService.class) {
                    mConnectThread = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                cancel();
                onCannotConnect();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "Unable to close bluetooth socket");
                e2.printStackTrace();
            }
        }
    }
}