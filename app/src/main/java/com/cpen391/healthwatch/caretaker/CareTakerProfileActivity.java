package com.cpen391.healthwatch.caretaker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.user.UserProfileOperator;
import com.cpen391.healthwatch.util.FadeInNetworkImageView;
import com.cpen391.healthwatch.util.StandardDividerItemDecoration;

/**
 * Created by william on 2018-04-04.
 * This activity allows patients to view a caretaker's profile.
 */
public class CareTakerProfileActivity extends AppCompatActivity {
    private String mCaretaker;
    private RecyclerView mRecyclerView;
    private PatientListAdapter mPatientListAdapter;
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
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.patient_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mPatientListAdapter = new PatientListAdapter(this);
        mRecyclerView.setAdapter(mPatientListAdapter);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(PatientListAdapter.TYPE_HEADER, 0);
        RecyclerView.ItemDecoration dividerItemDecoration = new StandardDividerItemDecoration(getApplicationContext(), R.drawable.inset_divider);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
