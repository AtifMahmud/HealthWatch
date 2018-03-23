package com.cpen391.healthwatch.Caretaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.ServerCallback;
import com.cpen391.healthwatch.util.GlobalFactory;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MealPlanActivity extends AppCompatActivity {

    EditText mealNumberText;
    TimePicker timePicker;
    EditText item1Text;
    EditText item2Text;
    EditText item3Text;
    MultiAutoCompleteTextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        Intent i = getIntent();
        final String name = i.getStringExtra("name");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        mealNumberText = findViewById(R.id.meal);
        timePicker = findViewById(R.id.time);
        item1Text = findViewById(R.id.item1);
        item2Text = findViewById(R.id.item2);
        item3Text = findViewById(R.id.item3);
        descriptionText = findViewById(R.id.description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText("for " + name);

        final Button sendButton = findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getNSendData(name);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void getNSendData(String username) throws JSONException{

         String mealNumber = mealNumberText.toString();
         String time = timePicker.toString();
         String item1 = item1Text.toString();
         String item2 = item2Text.toString();
         String item3 = item3Text.toString();
         String description = descriptionText.toString();

         JSONArray array = new JSONArray();
         JSONObject usernameID = new JSONObject();
         JSONObject data = new JSONObject();

        try {
            data.put("meal", mealNumber);
            data.put("time", time);
            data.put("item1", item1);
            data.put("item2", item2);
            data.put("item3", item3);
            data.put("description", description);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            usernameID.put("username", username);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        array.put(username);
        array.put(data);

        GlobalFactory.getServerInterface().asyncPost("/user/diet-plan", array.toString(), new ServerCallback() {
            @Override
            public void onSuccessResponse(String response) {
               Log.e("ServerCallback", "Success");
            }
        });
    }
}
