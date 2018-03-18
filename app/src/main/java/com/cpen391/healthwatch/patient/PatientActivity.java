/**
 *
 * Activity for the patient activity page
 *
 * Sources:
 *      1. https://www.youtube.com/watch?v=InkQJ4riGyI
 *
 */


package com.cpen391.healthwatch.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.util.BitmapDecodeTask;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.FadeInNetworkImageView.OnLoadCompleteListener;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.cpen391.healthwatch.util.UploadImageTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientActivity extends AppCompatActivity {
    private String TAG = PatientActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private ProgressBar mImageProgressSpinner;
    private FadeInNetworkImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        mImageProgressSpinner = findViewById(R.id.image_upload_progress);
        mProfileImage = findViewById(R.id.image_cover);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalFactory.getUserSessionInterface().getUsername());
        setSupportActionBar(toolbar);
        setListeners();
        setupUserProfileImage();
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
        if (mImageProgressSpinner.getVisibility() == View.VISIBLE) {
            Toast.makeText(getApplicationContext(), "Please wait while image is loading", Toast.LENGTH_SHORT).show();
            return;
        }
        dispatchTakePhotoIntent();
    }

    private void setupUserProfileImage() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/user/image", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                getUserProfileImage(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 404) {
                    mImageProgressSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void getUserProfileImage(String imageFilePathJson) {
        try {
            String imageFilePath = new JSONObject(imageFilePathJson).getString("image");
            String url = ServerInterface.BASE_URL + "/gateway/user/image/" + imageFilePath;
            mProfileImage.setImageUrl(url, GlobalFactory.getAppControlInterface().getImageLoader());
            mProfileImage.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    mImageProgressSpinner.setVisibility(View.INVISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to take photo", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                uploadUserProfileImage();
            }
        }
    }

    private void uploadUserProfileImage() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(mCurrentPhotoPath);
        mImageProgressSpinner.setVisibility(View.VISIBLE);
        UploadImageTask uploadImageTask = new UploadImageTask("/gateway/user/image", "image",
                filePaths, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                mImageProgressSpinner.setVisibility(View.INVISIBLE);
                File imageFile = new File(mCurrentPhotoPath);
                if (imageFile.exists()) {
                    if (!imageFile.delete()) {
                        Log.d(TAG, "Unable to delete image file");
                    }
                }
                BitmapDecodeTask bitmapDecodeTask = new BitmapDecodeTask(mProfileImage);
                bitmapDecodeTask.execute(mCurrentPhotoPath);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImageProgressSpinner.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Unable to upload image", Toast.LENGTH_SHORT).show();
            }
        });
        uploadImageTask.execute();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
