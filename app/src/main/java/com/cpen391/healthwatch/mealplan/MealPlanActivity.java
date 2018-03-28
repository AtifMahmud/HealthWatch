package com.cpen391.healthwatch.mealplan;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.mealplan.ConfirmFieldsDialog.OnConfirmFieldsDialogListener;
import com.cpen391.healthwatch.mealplan.ConfirmUnsavedDialog.OnConfirmUnsavedDialogListener;
import com.cpen391.healthwatch.mealplan.MealPlanFormAdapter.HeaderViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

public class MealPlanActivity extends AppCompatActivity
        implements OnConfirmUnsavedDialogListener,
        OnConfirmFieldsDialogListener {
    private final String TAG = MealPlanActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MealPlanFormAdapter mMealPlanItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.meal_edit_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupMealEditItemList();
    }

    private void setupMealEditItemList() {
        mRecyclerView = findViewById(R.id.meal_plan_item_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mMealPlanItemAdapter = new MealPlanFormAdapter();
        mRecyclerView.setAdapter(mMealPlanItemAdapter);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(MealPlanFormAdapter.TYPE_HEADER, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meal_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                onCompleteMealFormPress();
                return true;
            case android.R.id.home:
                onBackArrowPress();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onCompleteMealFormPress() {
        if (validateFields()) {
            ConfirmFieldsDialog dialog = new ConfirmFieldsDialog();
            dialog.setListener(this);
            dialog.show(getFragmentManager(), "ConfirmFieldsDialog");
        }
    }

    /**
     * Check to see if the input fields are valid.
     *
     * @return true if all the input fields are valid, false otherwise.
     */
    private boolean validateFields() {
        HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
        boolean error = false;
        if (vh != null) {
            if (vh.mEditText.getText().toString().isEmpty()) {
                vh.mEditText.setError("Meal name is required");
                error = true;
            }
            if (mMealPlanItemAdapter.hasAllEmptyMealItems()) {
                Toast.makeText(this, "No meal items filled in", Toast.LENGTH_SHORT).show();
                error = true;
            }
            if (!validateTimePicker(vh.mTimePicker)) {
                Toast.makeText(this, "Incorrect time", Toast.LENGTH_SHORT).show();
                error = true;
            }
        }
        return !error;
    }

    private boolean validateTimePicker(TimePicker timePicker) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            return timePicker.validateInput();
        } else {
            int hour = getTimePickerHour(timePicker);
            int minute = getTimePickerMinute(timePicker);
            return hour < 24 && minute < 60;
        }
    }

    private int getTimePickerHour(TimePicker timePicker) {
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            return timePicker.getCurrentHour();
        } else {
            return timePicker.getHour();
        }
    }

    private int getTimePickerMinute(TimePicker timePicker) {
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            return timePicker.getCurrentMinute();
        } else {
            return timePicker.getMinute();
        }
    }


    private void onBackArrowPress() {
        HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
        if (!mMealPlanItemAdapter.hasAllEmptyMealItems() || !vh.mEditText.getText().toString().isEmpty()) {
            ConfirmUnsavedDialog dialog = new ConfirmUnsavedDialog();
            dialog.setOnConfirmUnsavedListener(this);
            dialog.show(getFragmentManager(), "ConfirmUnsavedDialog");
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBackArrowPress();
    }

    @Override
    public void onConfirmUnsavedDialogPositiveClick() {
        finish();
    }

    @Override
    public void onConfirmUnsavedDialogNegativeClick() {
        // Does nothing
    }

    @Override
    public void onConfirmFieldsDialogPositiveClick() {
        String mealPlanJson = obtainMealPlanJsonString();
        Log.d(TAG, "Meal Plan json object: " + mealPlanJson);
        /*
        GlobalFactory.getServerInterface().asyncPost("/gateway/user/meal-plan", null, mealPlanJson,
                new ServerCallback() {
                    @Override
                    public void onSuccessResponse(String response) {

                    }
                }, new ServerErrorCallback() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });*/
        finish();
    }

    private String obtainMealPlanJsonString() {
        try {
            HeaderViewHolder vh = (HeaderViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
            JSONObject mealTimeObj = new JSONObject()
                    .put("hour", getTimePickerHour(vh.mTimePicker))
                    .put("minute", getTimePickerMinute(vh.mTimePicker));
            JSONObject mealJsonObj = new JSONObject()
                    .put("name", vh.mEditText.getText().toString())
                    .put("time", mealTimeObj)
                    .put("items", mMealPlanItemAdapter.getMealItems());
            return new JSONObject()
                    .put("meal", mealJsonObj)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public void onConfirmFieldsDialogNegativeClick() {
        // Does nothing
    }
}
