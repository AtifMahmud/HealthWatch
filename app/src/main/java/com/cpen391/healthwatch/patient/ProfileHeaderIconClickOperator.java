package com.cpen391.healthwatch.patient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.cpen391.healthwatch.caretaker.CareTakerProfileActivity;
import com.cpen391.healthwatch.patient.PatientProfileAdapter.ProfileHeaderIconClickListener;

/**
 * Created by william on 2018-04-04.
 * Activities can delegate profile header icon clicks to this class.
 */
public class ProfileHeaderIconClickOperator implements ProfileHeaderIconClickListener {
    public static final int LOCATION_ICON_CLICK = 100;
    private Activity mActivity;
    private String mProfileName;

    /**
     *
     * @param activity the activity that wants to register this click listener.
     * @param username the username that profile of user belongs to.
     */
    ProfileHeaderIconClickOperator(Activity activity, String username) {
        mActivity = activity;
        mProfileName = username;
    }

    @Override
    public void onPhoneIconClick(String phoneNumber) {
        if (!phoneNumber.isEmpty()) {
            String uri = "tel:" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onCaretakerIconClick(String caretaker) {
        if (!caretaker.isEmpty()) {
            Intent intent = new Intent(mActivity, CareTakerProfileActivity.class);
            intent.putExtra("caretaker", caretaker);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onLocationIconClick() {
        Intent data = new Intent();
        data.putExtra("username", mProfileName);
        mActivity.setResult(LOCATION_ICON_CLICK, data);
        mActivity.finish();
    }
}
