package com.cpen391.healthwatch.voice;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by william on 2018-04-02.
 * This class is used to process audio data to execute certain voice commands.
 */
public class VoiceCommand {
    private static final String TAG = VoiceCommand.class.getSimpleName();
    private final int SAMPLING_RATE;
    private Context mContext;

    public VoiceCommand(Context context, int samplingRate) {
        SAMPLING_RATE = samplingRate;
        mContext = context;
    }

    /**
     * Process 8-bit audio voice samples.
     *
     * @param data8bit the 8 bit audio data to be processed.
     * @param sampleSize the number of 8 bit samples to be processed.
     */
    public void processVoice8bit(byte[] data8bit, int sampleSize) {
        byte[] data16bit = audio8to16bit(data8bit, sampleSize);

        String speechRecognitionRequest = obtainSpeechRecognitionRequest(data16bit);
        String url = getSpeechApiUrl();
        Log.d(TAG, "Sending request to google speech api");
        GlobalFactory.getServerInterface().asyncPost2(url, speechRecognitionRequest, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained response from google speech API: " + response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error while sending google speech api");
                error.printStackTrace();
                if (error.networkResponse != null) {
                    Log.d(TAG, "Obtained status code from google speech API: " + error.networkResponse.statusCode);
                    Log.d(TAG, new String(error.networkResponse.data));
                }
            }
        });
    }

    private String getSpeechApiUrl() {
        return "https://speech.googleapis.com/v1/speech:recognize?key=" + mContext.getString(R.string.google_speech_to_text_key);
    }

    private String obtainSpeechRecognitionRequest(byte[] data16bit) {
        try {
            String encodedAudio = Base64.encodeToString(data16bit, Base64.NO_WRAP);
            JSONObject audioJSON = new JSONObject()
                    .put("content", encodedAudio);
            JSONObject configJSON = new JSONObject()
                    .put("languageCode", "en-US")
                    .put("sampleRateHertz", SAMPLING_RATE)
                    .put("encoding", "LINEAR16");
            return new JSONObject()
                    .put("audio", audioJSON)
                    .put("config", configJSON)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    /**
     * Convert 8 bit audio data to 16 bit audio data.
     * @param data8bit the 8 bit audio data to be converted.
     * @param sampleSize the number of 8 bit samples in the byte array.
     */
    private byte[] audio8to16bit(byte[] data8bit, int sampleSize) {
        byte[] outBuf = new byte[sampleSize * 2];
        short val;
        for (int i = 0; i < sampleSize; i++) {
            val = (short) ((data8bit[i] - 128) << 8);
            outBuf[i * 2] = (byte) (val >> 8);
            outBuf[i * 2 + 1] = (byte) val;
        }
        return outBuf;
    }
}
