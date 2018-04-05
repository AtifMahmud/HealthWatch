package com.cpen391.healthwatch.patient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.patient.PatientProfileAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.cpen391.healthwatch.util.LocationMethods;

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
    private PatientProfileAdapter mPatientProfileAdapter;
    private LocationMethods mLocationOperator;

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
        mLocationOperator = new LocationMethods(this);
        mLocationOperator.setLocationData(getIntent().getStringExtra("location"));
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

    private void getPatientBPM(String username) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        String path = String.format(Locale.CANADA, "/gateway/user/%s", username);
        GlobalFactory.getServerInterface().asyncGet(path, headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained BPM response: " + response);
                parseBPM(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "getting patient BPM obtained error");
            }
        });
    }

    private void parseBPM(String response) {
        try {
            JSONObject userProfile = new JSONObject(response);
            JSONObject bpmObj = userProfile.getJSONObject("bpm");
            String maxBPM = bpmObj.getString("max");
            String minBPM = bpmObj.getString("min");
            setBPMText(maxBPM, minBPM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBPMText(String maxBPM, String minBPM) {

    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_profile_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientProfileAdapter = new PatientProfileAdapter(this);
        mRecyclerView.setAdapter(mPatientProfileAdapter);
        // Set it so that header is not recycled.
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(PatientProfileAdapter.TYPE_HEADER, 0);
        mPatientProfileAdapter.setProfileHeaderIconClickListener(new ProfileHeaderIconClickOperator(this, mPatientName));
    }

    private void setupUserProfile(String response) {
        mImageOperator.getUserProfileImage(response, mProfileImage);
        HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
        ProfileHeaderOperator.displayProfileHeaderInfo(response, vh, mLocationOperator);
    }
}
