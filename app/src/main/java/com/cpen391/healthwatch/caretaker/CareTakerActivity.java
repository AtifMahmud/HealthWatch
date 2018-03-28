package com.cpen391.healthwatch.caretaker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.caretaker.PatientListAdapter.PatientItemClickListener;
import com.cpen391.healthwatch.mealplan.MealPlanActivity;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.user.UserProfileOperator.UserProfileImageListener;
import com.cpen391.healthwatch.util.BitmapDecodeTask;
import com.cpen391.healthwatch.util.BitmapDecodeTask.ImageDecodeCallback;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;

public class CareTakerActivity extends AppCompatActivity {
    private final String TAG = CareTakerActivity.class.getSimpleName();

    private UserProfileOperator mImageOperator;
    private FadeInNetworkImageView mProfileImage;
    private PatientListAdapter mPatientListAdapter;

    private String [] mDataset = {"Frank Tai", "Jack Guo"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_taker);
        // Order of statement matters.
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalFactory.getUserSessionInterface().getUsername());
        setSupportActionBar(toolbar);
        setupRecyclerView();
        mImageOperator = new UserProfileOperator();
        mProfileImage = findViewById(R.id.image_cover);
        mImageOperator.setupUserProfileImage(mProfileImage);
        setListeners();
        mImageOperator.setupUserProfileImage(mProfileImage);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.patient_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mPatientListAdapter = new PatientListAdapter(mDataset);
        recyclerView.setAdapter(mPatientListAdapter);
        ItemDecoration dividerItemDecoration = new PatientListDividerItemDecoration(getApplicationContext(), R.drawable.divider);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserProfileImageButtonClick();
            }
        });
        mPatientListAdapter.setOnPatientItemClickListener(new PatientItemClickListener() {
            @Override
            public void onEditClick(String patientName) {
                Intent intent = new Intent(CareTakerActivity.this, MealPlanActivity.class);
                intent.putExtra("name", patientName);
                startActivity(intent);
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
    }
}
