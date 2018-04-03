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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.bluetooth.BluetoothService;
import com.cpen391.healthwatch.bluetooth.BluetoothService.OnBluetoothDataListener;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.user.UserProfileOperator.UserProfileImageListener;
import com.cpen391.healthwatch.util.BitmapDecodeTask;
import com.cpen391.healthwatch.util.BitmapDecodeTask.ImageDecodeCallback;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class PatientActivity extends AppCompatActivity {
    private String TAG = PatientActivity.class.getSimpleName();

    private FadeInNetworkImageView mProfileImage;
    private TextView mBPMText;

    private boolean mShouldUnbindBluetooth;
    private BluetoothService mBluetoothService;
    private UserProfileOperator mImageUploader;

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

        mImageUploader = new UserProfileOperator();
        mProfileImage = findViewById(R.id.image_cover);
        mBPMText = findViewById(R.id.BPM);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalFactory.getUserSessionInterface().getUsername());
        setSupportActionBar(toolbar);
        setListeners();
        mImageUploader.setupUserProfileImage(mProfileImage);
        doBindService();
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        mRecyclerView = findViewById(R.id.patient_profile_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientProfileAdapter = new PatientProfileAdapter(this);
        mRecyclerView.setAdapter(mPatientProfileAdapter);
        //RecyclerView.ItemDecoration dividerItemDecoration  = new MealListDividerItemDecoration(getApplicationContext(), R.drawable.inset_divider);
        //mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupBluetoothServiceCallbacks() {
        mBluetoothService.setOnDataReceiveListener(new OnBluetoothDataListener() {
            @Override
            public void onDataReceived(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBPMText != null) {
                            mBPMText.setText(String.format(Locale.CANADA, "%s BPM", data));
                        }
                    }
                });
            }

            @Override
            public void onDataReceived(final byte[] data, int offset, int size) {
                if (!isExternalStorageWritable()) {
                    Log.d(TAG, "Unable to write to external storage");
                    return;
                }
                Log.d(TAG, "Writing to file: " + getFilesDir().getAbsolutePath());
                String filename = "myFile";
                File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir == null) {
                    Log.e(TAG, "External directory is null");
                    return;
                }
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e(TAG, "Directory not created");
                    return;
                }
                File file = new File(dir, filename);
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(data, offset, size);
                    outputStream.close();
                    Log.d(TAG, "Completed file write");
                } catch (IOException e) {
                    Log.d(TAG, "exception when writing to file");
                    e.printStackTrace();
                }
            }
        });
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void setListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserProfileImageButtonClick();
            }
        });

        mProfileImage.setOnLoadCompleteListener(new FadeInNetworkImageView.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                Log.d(TAG, "Loading image complete");
            }
        });

    }

    private void uploadUserProfileImageButtonClick() {
        if (mImageUploader.isSendingImage()) {
            Toast.makeText(getApplicationContext(), "Please wait while image is loading", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageUploader.dispatchTakePhotoIntent(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserProfileOperator.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mImageUploader.uploadUserProfileImage(this, new UserProfileImageListener() {
                @Override
                public void onUserProfileImageUploaded() {
                    Log.d(TAG, "decoding uploaded image");
                    BitmapDecodeTask bitmapDecodeTask = new BitmapDecodeTask(new ImageDecodeCallback() {
                        @Override
                        public void callback(Bitmap bitmap) {
                            mProfileImage.setLocalImageBitmap(bitmap);
                        }
                    });
                    bitmapDecodeTask.execute(mImageUploader.getCurrentPhotoPath());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageUploader.deleteCurrentPhoto();
        doUnbindService();
    }
}
