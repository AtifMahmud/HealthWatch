package com.cpen391.healthwatch.Caretaker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.healthwatch.R;

public class CareTakerActivity extends AppCompatActivity {
    private String [] dataset = {"FOO", "BAR", "FOOBAR", "FIZZ", "BUZZ", "FIZZBUZZ", "FIZZBAR", "FOOBUZZ", "BAR", "FOOBAR", "FIZZ", "BUZZ", "FIZZBUZZ", "FIZZBAR", "FOOBUZZ"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_taker);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new MyAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void mealButtonPressed(){
        Toast.makeText(this, "Pressed ", Toast.LENGTH_SHORT).show();
        Log.e("PRESS", "FAB pressed");
    }


}
