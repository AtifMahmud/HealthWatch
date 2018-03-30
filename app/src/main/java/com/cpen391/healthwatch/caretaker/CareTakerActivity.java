package com.cpen391.healthwatch.caretaker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.caretaker.PatientListAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.caretaker.PatientListAdapter.PatientItemClickListener;
import com.cpen391.healthwatch.mealplan.MealPlanActivity;
import com.cpen391.healthwatch.patient.PatientProfileActivity;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.user.UserProfileOperator.UserProfileImageListener;
import com.cpen391.healthwatch.util.AnimationOperator;
import com.cpen391.healthwatch.util.BitmapDecodeTask;
import com.cpen391.healthwatch.util.BitmapDecodeTask.ImageDecodeCallback;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CareTakerActivity extends AppCompatActivity {
    private final String TAG = CareTakerActivity.class.getSimpleName();

    private static final int REQUEST_EDIT_MEAL_PLAN = 10;
    private UserProfileOperator mImageOperator;
    private FadeInNetworkImageView mProfileImage;
    private PatientListAdapter mPatientListAdapter;
    private RecyclerView mRecyclerView;

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
        setListeners();
        getUserInfoFromServer();
        getPatientsFromServer();
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientListAdapter = new PatientListAdapter(this);
        mRecyclerView.setAdapter(mPatientListAdapter);
        ItemDecoration dividerItemDecoration = new PatientListDividerItemDecoration(getApplicationContext(), R.drawable.inset_divider);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void getUserInfoFromServer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/user", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained user profile: " + response);
                setupUserProfile(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Trying to obtain user profile obtained error");
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

    private void getPatientsFromServer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/patients", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Patients obtained from server: " + response);
                addPatientsToList(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void addPatientsToList(String response) {
        try {
            JSONArray patients = new JSONArray(response);
            for (int i = 0; i < patients.length(); i++) {
                mPatientListAdapter.addPatient(patients.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        mPatientListAdapter.setOnPatientItemClickListener(new PatientItemClickListener() {
            @Override
            public void onEditClick(String patientName) {
                Intent intent = new Intent(CareTakerActivity.this, MealPlanActivity.class);
                intent.putExtra(MealPlanActivity.PATIENT_NAME, patientName);
                startActivityForResult(intent, REQUEST_EDIT_MEAL_PLAN);
            }
            @Override
            public void onProfileClick(String patientName) {
                Intent intent = new Intent(CareTakerActivity.this, PatientProfileActivity.class);
                intent.putExtra(PatientProfileActivity.PATIENT_NAME, patientName);
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
        } else if (requestCode == REQUEST_EDIT_MEAL_PLAN && resultCode == RESULT_OK) {
            sendMealToServer(data.getStringExtra(MealPlanActivity.MEAL_DATA));
        }
    }

    private void sendMealToServer(String mealPlanJson) {
        Log.d(TAG, "Meal Plan json object: " + mealPlanJson);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncPost("/gateway/user/diet-plan", headers, mealPlanJson,
                new ServerCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Meal Plan Sent!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }, new ServerErrorCallback() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageOperator.deleteCurrentPhoto();
    }
}
