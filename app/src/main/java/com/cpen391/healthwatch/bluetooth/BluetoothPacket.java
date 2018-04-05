package com.cpen391.healthwatch.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by william on 2018-04-02.
 * A packet that holds data send from bluetooth with the healthwatch protocol.
 */

class BluetoothPacket {
    private static final String TAG = BluetoothPacket.class.getSimpleName();
    static final int AUDIO_SAMPLE_RATE = 9600;
    private byte[] mAudioBuffer;
    private int mAudioBufferIndex;
    private final int mAudioBufferSize;
    private int mBPM;

    BluetoothPacket() {
        mAudioBufferSize = 100 * 1024;
        mAudioBuffer = new byte[mAudioBufferSize];
        mAudioBufferIndex = 0;
        mBPM = 0;
    }

    /**
     * @return the current length of the audio received, in seconds.
     */
    float getAudioLength() {
        return ((float) mAudioBufferIndex) / AUDIO_SAMPLE_RATE;
    }

    byte[] getAudioSamples() {
        return mAudioBuffer;
    }

    int getAudioSampleSize() {
        return mAudioBufferIndex;
    }

    void clearAudioBuffer() {
        mAudioBufferIndex = 0;
    }

    /**
     * Reads byte data from bluetooth. This method blocks till some data that follows the
     * healthwatch protocol is received.
     *
     * @param inputStream the input stream from bluetooth.
     * @throws IOException if problem occurred during read.
     */
    void readData(InputStream inputStream) throws IOException {
        final int BUF_SIZE = 1024;
        byte[] buf = new byte[BUF_SIZE];
        int bytes = inputStream.read(buf);
        if (bytes < 0) {
            throw new IOException("End of stream reached");
        } else {
            processBytes(buf, bytes);
        }
    }


    private void processBytes(byte[] buf, int len) {
        final byte AUDIO_FLAG = 0x01;
        for (int i = 0; i < len; i++) {
            if ((buf[i] & AUDIO_FLAG) != 0) {
                if (mAudioBufferIndex == mAudioBufferSize) {
                    Log.d(TAG, " Audio buffer overflow");
                    continue; // Too many audio samples not yet cleared.
                }
                mAudioBuffer[mAudioBufferIndex++] = buf[i];
            } else {
                mBPM = buf[i];
            }
        }
    }

    public int getCurrentBPM() {
        return mBPM;
    }

}
