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
import org.json.JSONException;
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

    private JSONObject breakfast = new JSONObject();
    private JSONObject lunch = new JSONObject();
    private JSONObject dinner = new JSONObject();
    private JSONArray mealList = new JSONArray();

    PatientProfileAdapter(Context context) {

        try {
            // Add dummy JSON
            breakfast.put("title", "Breakfast");
            breakfast.put("time", "9");
            breakfast.put("item1", "Oatmeal with skim milk and strawberries");
            breakfast.put("item2", "2 Boiled Eggs");
        } catch (JSONException e){
            e.printStackTrace();
        }


        try {
            // Add dummy JSON
            lunch.put("title", "Lunch");
            lunch.put("time", "12");
            lunch.put("item1", "Caesar Salad");
            lunch.put("item2", "Grilled Chicken");
        } catch (JSONException e){
            e.printStackTrace();
        }


        try {
            // Add dummy JSON
            dinner.put("title", "Dinner");
            dinner.put("time", "5");
            dinner.put("item1", "Spaghetti");
            dinner.put("item2", "250gm Beef Steak");
        } catch (JSONException e){
            e.printStackTrace();
        }

        mealList.put(breakfast);
        mealList.put(lunch);
        mealList.put(dinner);

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
            try {
                JSONObject meal = mealList.getJSONObject(position - 1);
                vh.mTitle.setText(meal.getString("title"));
                vh.mTime.setText(meal.getString("time"));
                vh.mItem1.setText(meal.getString("item1"));
                vh.mItem2.setText(meal.getString("item2"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1 + mealList.length();
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
        TextView mTitle;
        TextView mTime;
        TextView mItem1;
        TextView mItem2;

        MealViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.meal_title);
            mTime = view.findViewById(R.id.meal_time);
            mItem1 = view.findViewById(R.id.meal_item1);
            mItem2 = view.findViewById(R.id.meal_item2);
        }
    }
}
