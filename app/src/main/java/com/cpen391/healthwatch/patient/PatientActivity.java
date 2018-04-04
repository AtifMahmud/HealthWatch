package com.cpen391.healthwatch.patient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import com.cpen391.healthwatch.util.StandardDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PatientActivity extends AppCompatActivity {
    private String TAG = PatientActivity.class.getSimpleName();

    private FadeInNetworkImageView mProfileImage;

    private boolean mShouldUnbindBluetooth;
    private BluetoothService mBluetoothService;
    private UserProfileOperator mImageOperator;

    private RecyclerView mRecyclerView;
    private PatientProfileAdapter mPatientProfileAdapter;

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
        mProfileImage = findViewById(R.id.image_cover);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalFactory.getUserSessionInterface().getUsername());
        setSupportActionBar(toolbar);
        setListeners();
        doBindService();
        setupRecyclerView();
        getProfileInfoFromServer();
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
        try {
            mImageOperator.getUserProfileImage(response, mProfileImage);
            JSONObject userDataJSON = new JSONObject(response).getJSONObject("data");
            String phoneNumber = userDataJSON.getString("phone");
            HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
            vh.mProfilePhoneNumber.setVisibility(View.INVISIBLE);
            vh.mProfilePhoneNumber.setText(phoneNumber);
            AnimationOperator.fadeInAnimation(vh.mProfilePhoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(){
        mRecyclerView = findViewById(R.id.patient_profile_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientProfileAdapter = new PatientProfileAdapter(this);
        mRecyclerView.setAdapter(mPatientProfileAdapter);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(PatientProfileAdapter.TYPE_HEADER, 0);
    }

    private void setupBluetoothServiceCallbacks() {
        mBluetoothService.setOnDataReceiveListener(new OnBluetoothDataListener() {
            @Override
            public void onDataReceived(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPatientProfileAdapter.setPatientBPM(data);
                    }
                });
            }

            @Override
            public void onDataReceived(final byte[] data, int offset, int size) {
                Log.d(TAG, "Not doing anything with byte data");
            }
        });
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
        doUnbindService();
    }
}
