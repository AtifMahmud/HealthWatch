package com.cpen391.healthwatch.patient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atifm on 4/2/2018.
 */

public class MealListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mDataset;
    private Context mContext;


    MealListAdapter(Context context){
        mDataset = new ArrayList<>();
        mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
