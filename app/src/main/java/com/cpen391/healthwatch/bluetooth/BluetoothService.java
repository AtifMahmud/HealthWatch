package com.cpen391.healthwatch.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.cpen391.healthwatch.signal.VAD;
import com.cpen391.healthwatch.voice.VoiceCommand;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * References: google's Bluetooth Chat example.
 */
public class BluetoothService extends Service {

    public interface OnBluetoothDataListener {
        void onDataReceived(String data);
    }
    public static final String BLUETOOTH_ADDRESS = "BT_ADDR";
    public static final int CONNECT_FAILED = 1;

    private final String TAG = BluetoothService.class.getSimpleName();

    // UUID for connecting to RN-42 bluetooth dongle.
    private static final UUID RN_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private VAD mVAD;
    private VoiceCommand mVoiceCommand;

    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private String mHealthWatchAddress;
    private OnBluetoothDataListener mListener;

    private BluetoothPacket mBluetoothPacket;

    private boolean mEndingService;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            int command = message.what;
            Log.d(TAG, "Handling message command: " + command);
            switch(command) {
                case CONNECT_FAILED:
                    Toast.makeText(getApplicationContext(), (String)message.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    };

    private final Binder mBinder = new BluetoothBinder();

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onCreate() {
        mVoiceCommand = new VoiceCommand(getApplicationContext(), BluetoothPacket.AUDIO_SAMPLE_RATE);
        mBluetoothPacket = new BluetoothPacket();
        mVAD = new VAD(5000, BluetoothPacket.AUDIO_SAMPLE_RATE);
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

        ConnectedThread(BluetoothSocket socket) {
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
            long timer = System.currentTimeMillis();
            final int BPM_UPDATE_INTERVAL = 500;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    //Log.d(TAG," reading bluetooth data");
                    mBluetoothPacket.readData(mmInputStream);
                    if (mBluetoothPacket.getAudioLength() >= 5) {
                        int audioSampleSize = mBluetoothPacket.getAudioSampleSize();
                        byte[] samples = mBluetoothPacket.getAudioSamples();
                        mBluetoothPacket.clearAudioBuffer();
                        Log.d(TAG," Sending voice to VAD");
                        boolean isSpeech = mVAD.vad(samples);
                        Log.d(TAG, "speech detected: " + isSpeech);
                        if (isSpeech) {
                            mVoiceCommand.processVoice8bit(samples, audioSampleSize);
                        }
                    }
                    if (System.currentTimeMillis() - timer > BPM_UPDATE_INTERVAL) {
                        sendReceivedData(Integer.toString(mBluetoothPacket.getCurrentBPM()));
                        timer = System.currentTimeMillis();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    onConnectionLost();
                    break;
                }
            }
        }

        void cancel() {
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

        ConnectThread(String macAddr) {
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

        private void onCannotConnect() {
            Log.e(TAG, "Cannot connect to HealthWatch");
            Message message = mHandler.obtainMessage(CONNECT_FAILED, "Cannot connect to healthwatch device");
            message.sendToTarget();
            Log.e(TAG, "Retrying connection to HealthWatch");
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "Unable to close bluetooth socket");
                e2.printStackTrace();
            }
        }
    }
}