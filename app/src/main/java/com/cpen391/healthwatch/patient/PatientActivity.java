package com.cpen391.healthwatch.patient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.bluetooth.BluetoothService;
import com.cpen391.healthwatch.bluetooth.BluetoothService.OnBluetoothDataListener;
import com.cpen391.healthwatch.patient.PatientProfileAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.user.UserProfileOperator.UserProfileImageListener;
import com.cpen391.healthwatch.util.AnimationOperator;
import com.cpen391.healthwatch.util.BitmapDecodeTask;
import com.cpen391.healthwatch.util.BitmapDecodeTask.ImageDecodeCallback;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.cpen391.healthwatch.util.LocationMethods;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientActivity extends AppCompatActivity {
    private String TAG = PatientActivity.class.getSimpleName();

    private FadeInNetworkImageView mProfileImage;

    private boolean mShouldUnbindBluetooth;
    private BluetoothService mBluetoothService;
    private UserProfileOperator mImageOperator;
    private LocationMethods mLocationOperator;

    private RecyclerView mRecyclerView;
    private PatientProfileAdapter mPatientProfileAdapter;
    // Delegates the bpm keeping functionality
    private MaxMinHandler mBpmOperator;


    private ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) iBinder).getService();
            setupBluetoothServiceCallbacks();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothService = null;
            Log.d(TAG, "Service disconnected from Patient Activity");
        }
    };

    private void doBindService() {
        if (bindService(new Intent(PatientActivity.this, BluetoothService.class), mBluetoothServiceConnection,
                Context.BIND_AUTO_CREATE)) {
            mShouldUnbindBluetooth = true;
        } else {
            Log.e(TAG, "Unable to request bluetooth service from Patient Activity");
        }
    }

    private void doUnbindService() {
        if (mShouldUnbindBluetooth) {
            unbindService(mBluetoothServiceConnection);
            mShouldUnbindBluetooth = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        mImageOperator = new UserProfileOperator();
        mLocationOperator = new LocationMethods(this);
        mProfileImage = findViewById(R.id.image_cover);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalFactory.getUserSessionInterface().getUsername());
        setSupportActionBar(toolbar);
        setListeners();
        doBindService();
        setupRecyclerView();
        getProfileInfoFromServer();
        Intent data = getIntent();
        String locationDataJSON = data.getStringExtra("location");
        mLocationOperator.setLocationData(locationDataJSON);
        mBpmOperator = new MaxMinHandler(this);
        mBpmOperator.init();
    }

    private void getProfileInfoFromServer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/user", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained own user profile: " + response);
                setupUserProfile(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Trying to obtain own user profile obtained error");
            }
        });
    }

    private void setupUserProfile(String response) {
        mImageOperator.getUserProfileImage(response, mProfileImage);
        HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
        ProfileHeaderOperator.displayProfileHeaderInfo(response, vh, mLocationOperator);
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_profile_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientProfileAdapter = new PatientProfileAdapter(this);
        mRecyclerView.setAdapter(mPatientProfileAdapter);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(PatientProfileAdapter.TYPE_HEADER, 0);
        mPatientProfileAdapter.setProfileHeaderIconClickListener(new ProfileHeaderIconClickOperator(this,
                GlobalFactory.getUserSessionInterface().getUsername()));
    }

    private void setupBluetoothServiceCallbacks() {
        mBluetoothService.setOnDataReceiveListener(new OnBluetoothDataListener() {
            @Override
            public void onDataReceived(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBpmOperator.update(data, System.currentTimeMillis());
                        setMaxMinBPM();
                    }
                });
            }
        });
    }

    private void setMaxMinBPM() {
        String maxBPM = mBpmOperator.getMax();
        String minBPM = mBpmOperator.getMin();
        String maxBpmDisplayString = String.format(Locale.CANADA, "Max: %s BPM", maxBPM);
        String minBpmDisplayString = String.format(Locale.CANADA, "Min: %s BPM", minBPM);
        HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
        if (vh != null) {
            vh.mProfileBPMMaxText.setText(maxBpmDisplayString);
            vh.mProfileBPMMinText.setText(minBpmDisplayString);
            if (vh.mProfileBPMMaxText.getVisibility() == View.INVISIBLE) {
                AnimationOperator.fadeInAnimation(vh.mProfileBPMMaxText);
            }
            if (vh.mProfileBPMMinText.getVisibility() == View.INVISIBLE) {
                AnimationOperator.fadeInAnimation(vh.mProfileBPMMinText);
            }
        }
    }

    private void setListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserProfileImageButtonClick();
            }
        });
    }

    private void uploadUserProfileImageButtonClick() {
        if (mImageOperator.isSendingImage()) {
            Toast.makeText(getApplicationContext(), "Please wait while image is loading", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageOperator.dispatchTakePhotoIntent(this);
    }

    private void updatePatientBPM(JSONObject bpm) {
        Log.d(TAG, "Updating patient's BPM");
        String bpmString = bpm.toString();
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncPost("/gateway/bpm", headers, bpmString, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "updating user bpm obtained response: " + response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "updating user bpm obtained error");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserProfileOperator.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mImageOperator.uploadUserProfileImage(this, new UserProfileImageListener() {
                @Override
                public void onUserProfileImageUploaded() {
                    Log.d(TAG, "decoding uploaded image");
                    BitmapDecodeTask bitmapDecodeTask = new BitmapDecodeTask(new ImageDecodeCallback() {
                        @Override
                        public void callback(Bitmap bitmap) {
                            mProfileImage.setLocalImageBitmap(bitmap);
                        }
                    });
                    bitmapDecodeTask.execute(mImageOperator.getCurrentPhotoPath());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageOperator.deleteCurrentPhoto();
        mBpmOperator.save();
        doUnbindService();
    }
}
