package com.cpen391.healthwatch.patient;

import android.view.View;

import com.cpen391.healthwatch.patient.PatientProfileAdapter.HeaderViewHolder;
import com.cpen391.healthwatch.util.AnimationOperator;
import com.cpen391.healthwatch.util.LocationMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by william on 2018-04-04.
 * This class operates on the header of the patient profile to display info properly.
 */
class ProfileHeaderOperator {
    static void displayProfileHeaderInfo(String response, HeaderViewHolder vh, LocationMethods locationOperator) {
        try {
            JSONObject userObj = new JSONObject(response);
            JSONObject userDataJSON = userObj.getJSONObject("data");
            String phoneNumber = userDataJSON.getString("phone");
            String caretaker = userObj.getJSONArray("caretaker").getString(0);

            vh.mProfilePhoneNumber.setVisibility(View.INVISIBLE);
            vh.mProfilePhoneNumber.setText(phoneNumber);
            vh.mProfileCaretakerName.setVisibility(View.INVISIBLE);
            vh.mProfileCaretakerName.setText(caretaker);
            vh.mProfileLocationText.setVisibility(View.INVISIBLE);
            vh.mProfileLocationLabel.setVisibility(View.INVISIBLE);
            String city = locationOperator.getCity();
            if (city != null) {
                vh.mProfileLocationText.setText(locationOperator.getCity());
            }
            String locationUpdateTime = locationOperator.getTimeLastUpdated();
            if (locationUpdateTime != null) {
                vh.mProfileLocationLabel.setText(locationUpdateTime);
            }
            AnimationOperator.fadeInAnimation(vh.mProfilePhoneNumber);
            AnimationOperator.fadeInAnimation(vh.mProfileCaretakerName);
            AnimationOperator.fadeInAnimation(vh.mProfileLocationText);
            AnimationOperator.fadeInAnimation(vh.mProfileLocationLabel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
