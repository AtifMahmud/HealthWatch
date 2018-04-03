package com.cpen391.healthwatch.patient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PatientMeals extends AppCompatActivity {

    ServerCallback mServerCallback = new ServerCallback() {
        @Override
        public void onSuccessResponse(String response) {

            try{
                JSONObject responseJSON = new JSONObject(response);
                JSONArray dietArray = responseJSON.getJSONArray("diet");
                initializeFields(dietArray);

            } catch (JSONException e){
                Log.e("Exception", "JSON Exception");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_meals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // GET the mealplan
        GlobalFactory.getServerInterface().asyncGet("/user/diet-plan", mServerCallback);

    }

    public void initializeFields(JSONArray mJSONArray){

    }


}
