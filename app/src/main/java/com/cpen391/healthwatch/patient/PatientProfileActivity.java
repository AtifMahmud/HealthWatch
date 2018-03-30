package com.cpen391.healthwatch.patient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.patient.PatientProfileAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.util.AnimationOperator;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by william on 2018/3/29.
 * Allows other users' most likely caretakers to view patient's profile page.
 */
public class PatientProfileActivity extends AppCompatActivity {
    private static final String TAG = PatientProfileActivity.class.getSimpleName();
    public static final String PATIENT_NAME = "name";

    private String mPatientName;
    private FadeInNetworkImageView mProfileImage;
    private UserProfileOperator mImageOperator;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        mPatientName = getIntent().getStringExtra(PATIENT_NAME);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mPatientName);
        setSupportActionBar(toolbar);

        setupRecyclerView();
        mImageOperator = new UserProfileOperator();
        mProfileImage = findViewById(R.id.image_cover);
        getPatientInfoFromServer();

    }

    private void getPatientInfoFromServer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        String path = String.format(Locale.CANADA, "/gateway/patients/%s", mPatientName);
        GlobalFactory.getServerInterface().asyncGet(path, headers, new ServerCallback() {
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

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_profile_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        PatientProfileAdapter patientProfileAdapter = new PatientProfileAdapter(this);
        mRecyclerView.setAdapter(patientProfileAdapter);
        // Set it so that header is not recycled.
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(PatientProfileAdapter.TYPE_HEADER, 0);
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
}
