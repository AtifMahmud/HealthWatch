package com.cpen391.healthwatch.voice;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        GlobalFactory.getServerInterface().asyncPost2(url, null, speechRecognitionRequest, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained response from google speech API: " + response);
                processSpeechResponse(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error while sending to google speech api");
                error.printStackTrace();
                if (error.networkResponse != null) {
                    Log.d(TAG, "Obtained status code from google speech API: " + error.networkResponse.statusCode);
                    Log.d(TAG, new String(error.networkResponse.data));
                }
            }
        });
    }

    private void processSpeechResponse(String response) {
        try {
            JSONObject speechJSON = new JSONObject(response);
            JSONArray resultsJSON = speechJSON.getJSONArray("results");
            JSONArray alternatives = resultsJSON.getJSONObject(0).getJSONArray("alternatives");
            String transcript = alternatives.getJSONObject(0).getString("transcript");
            sendTranscriptToAI(transcript);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the ai assistant for processing.
     * @param transcript the message to send to the ai.
     */
    private void sendTranscriptToAI(String transcript) {
        String url = getAssistantApiUrl();
        String assistantRequestJSON = obtainAssistantRequest(transcript);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + mContext.getString(R.string.dialog_flow_api_client_access_token));
        GlobalFactory.getServerInterface().asyncPost2(url, headers, assistantRequestJSON,
                new ServerCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        Log.d(TAG, "Obtained response from google dialog flow API: " + response);
                        processAssistantResponse(response);
                    }
                }, new ServerErrorCallback() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error while sending to dialog flow api");
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            Log.d(TAG, "Obtained status code from dialog flow API: " + error.networkResponse.statusCode);
                            Log.d(TAG, new String(error.networkResponse.data));
                        }
                    }
                });
    }

    private void processAssistantResponse(String response) {
        try {
            JSONObject assistantResJSON = new JSONObject(response);
            String action = assistantResJSON.getJSONObject("result")
                    .getString("action");
            executeAction(action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void executeAction(String action) {
        switch(action) {
            case "emergency":
                Log.d(TAG, "executing emergency action");
                break;
            default:
                Log.d(TAG, "unknown action!");
        }
    }

    private String obtainAssistantRequest(String query) {
        try {
            return new JSONObject()
                    .put("lang", "en")
                    .put("query", query)
                    .put("sessionId", UUID.randomUUID())
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private String getAssistantApiUrl() {
        return "https://api.dialogflow.com/v1/query?v=20150910";
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
