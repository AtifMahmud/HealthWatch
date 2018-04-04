package com.cpen391.healthwatch.patient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public interface ProfileHeaderIconClickListener {
        void onPhoneIconClick(String phoneNumber);
        void onCaretakerIconClick(String caretaker);
        void onLocationIconClick();
    }

    static final int TYPE_HEADER = 0;
    private static final int TYPE_MEAL_ITEM = 1;
    private Context mContext;
    private HeaderViewHolder mHeaderViewHolder;

    private ProfileHeaderIconClickListener mListener;

    private List<JSONObject> mMealList;

    private JSONObject getTestMeal(String title, String time, List<String> items) {
        JSONObject meal = new JSONObject();
        try {
            meal.put("title", title);
            meal.put("time", time);
            JSONArray temp = new JSONArray(items);
            meal.put("items", temp);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return meal;
    }

    PatientProfileAdapter(Context context) {
        mMealList = new ArrayList<>();
        mContext = context;

        List<String> items = new ArrayList<>();
        items.add("Caesar Salad");
        items.add("Grilled Chicken");
        JSONObject meal = getTestMeal("Lunch", "12:00 PM", items);

        mMealList.add(meal);

        List<String> items2 = new ArrayList<>();
        items2.add("Chips");
        items2.add("Coke");
        items2.add("Soda");
        items2.add("Candy");
        JSONObject meal2 = getTestMeal("Dinner", "7:00 PM", items2);

        mMealList.add(meal2);
    }

    void setPatientBPM(String bpm) {
        if (mHeaderViewHolder != null) {
            Log.d("PatientProfileAdapter", "Not implemented");
        }
    }

    void setProfileHeaderIconClickListener(ProfileHeaderIconClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.list_patient_profile_header, parent, false);
            mHeaderViewHolder = new HeaderViewHolder(v);
            return mHeaderViewHolder;
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
            vh.bind(mMealList.get(position - 1));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            vh.bind();
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
        ImageView mProfileLocationIcon;
        ImageView mProfileCaretakerIcon;
        TextView mProfilePhoneNumber;
        TextView mProfileCaretakerName;
        TextView mHeaderText;

        HeaderViewHolder(View view) {
            super(view);
            mProfilePhoneNumber = view.findViewById(R.id.profile_phone_number);
            mProfileCaretakerName = view.findViewById(R.id.profile_caretaker);
            mProfilePhoneIcon = view.findViewById(R.id.profile_phone_icon);
            mProfileLocationIcon = view.findViewById(R.id.profile_location_icon);
            mProfileCaretakerIcon = view.findViewById(R.id.profile_caretaker_icon);
            mHeaderText = view.findViewById(R.id.header);

            mHeaderText.setText(R.string.meal_list_header);
            setListeners();
        }

        void bind() {
            setListeners();
        }

        private void setListeners() {
            mProfilePhoneIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        String phoneNumber = mProfilePhoneNumber.getText().toString();
                        mListener.onPhoneIconClick(phoneNumber);
                    }
                }
            });
            mProfileLocationIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onLocationIconClick();
                    }
                }
            });
            mProfileCaretakerIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        String caretaker = mProfileCaretakerName.getText().toString();
                        mListener.onCaretakerIconClick(caretaker);
                    }
                }
            });
        }

    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mTime;
        LinearLayout mLinearLayout;

        MealViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.meal_title);
            mTime = view.findViewById(R.id.meal_time);
            mLinearLayout = view.findViewById(R.id.meal_layout);
        }

        void bind(JSONObject meal) {
            try {
                mTitle.setText(meal.getString("title"));
                mTime.setText(meal.getString("time"));
                JSONArray mealItems = meal.getJSONArray("items");
                for (int i = 0; i < mealItems.length(); i++) {
                    TextView tv = (TextView) LayoutInflater.from(mContext)
                            .inflate(R.layout.single_meal_item, (ViewGroup) itemView, false);
                    tv.setText(mealItems.getString(i));
                    mLinearLayout.addView(tv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
