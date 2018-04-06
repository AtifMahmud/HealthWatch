package com.cpen391.healthwatch.caretaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.caretaker.CareTakerProfileAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.server.abstraction.ServerErrorCallback;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.util.AnimationOperator;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.GlobalFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william on 2018-04-04.
 * This activity allows patients to view a caretaker's profile.
 */
public class CareTakerProfileActivity extends AppCompatActivity {
    private static final String TAG = CareTakerProfileActivity.class.getSimpleName();
    private String mCaretaker;
    private RecyclerView mRecyclerView;
    private CareTakerProfileAdapter mCareTakerProfileAdapter;
    private UserProfileOperator mImageOperator;
    private FadeInNetworkImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_taker_profile);

        mCaretaker = getIntent().getStringExtra("username");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mCaretaker);
        setSupportActionBar(toolbar);
        setupRecyclerView();
        mImageOperator = new UserProfileOperator();
        mProfileImage = findViewById(R.id.image_cover);
        getCaretakerProfileFromServer();
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mCareTakerProfileAdapter = new CareTakerProfileAdapter(this);
        mRecyclerView.setAdapter(mCareTakerProfileAdapter);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(CareTakerProfileAdapter.TYPE_HEADER, 0);
    }

    private void getCaretakerProfileFromServer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", GlobalFactory.getUserSessionInterface().getUserToken());
        GlobalFactory.getServerInterface().asyncGet("/gateway/caretaker", headers, new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Log.d(TAG, "Obtained caretaker profile: " + response);
                setupUserProfile(response);
            }
        }, new ServerErrorCallback() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Trying to obtain caretaker profile obtained error");
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
}
