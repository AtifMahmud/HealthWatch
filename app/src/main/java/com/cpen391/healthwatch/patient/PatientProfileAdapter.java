package com.cpen391.healthwatch.patient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen391.healthwatch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 2018/3/29.
 *
 */

public class PatientProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int TYPE_HEADER = 0;
    static final int TYPE_MEAL_ITEM = 1;
    private Context mContext;

    private List<String> mMealList;
    private JSONObject meal = new JSONObject();
    private JSONArray mealList = new JSONArray();

    PatientProfileAdapter(Context context) {
        mMealList = new ArrayList<>();

        // Add dummy JSON
        for (String item : new String[]{"Pancakes", "Eggs", "Sausages", "Burgers"}) {

            // PArse and add items
            mMealList.add(item);
        }

        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.caretaker_page_header, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_MEAL_ITEM) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.list_meal_item, parent, false);
            return new MealViewHolder(v);
        }
        throw new IllegalArgumentException("Other views doesn't exist");
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder instanceof MealViewHolder) {
            MealViewHolder vh = (MealViewHolder) holder;

            // Add specific field to specific textview
            vh.mTextView.setText(mMealList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return 1 + mMealList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_MEAL_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfilePhoneIcon;
        TextView mProfilePhoneNumber;
        HeaderViewHolder(View view) {
            super(view);
            mProfilePhoneNumber = view.findViewById(R.id.profile_phone_number);
            mProfilePhoneIcon = view.findViewById(R.id.profile_phone_icon);
            mProfilePhoneIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNumber = mProfilePhoneNumber.getText().toString();
                    if (!phoneNumber.isEmpty()) {
                        String uri = "tel:" + phoneNumber;
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        MealViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.meal_item);
        }
    }
}
