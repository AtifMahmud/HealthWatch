package com.cpen391.healthwatch.patient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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

    void setMealList(JSONArray mealList)  {
        try {
            mMealList.clear();
            for (int i = 0; i < mealList.length(); i++) {
                mMealList.add(mealList.getJSONObject(i));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    PatientProfileAdapter(Context context) {
        mMealList = new ArrayList<>();
        mContext = context;
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
        TextView mProfileLocationText;
        TextView mProfileLocationLabel;
        TextView mProfileBPMMaxText;
        TextView mProfileBPMMinText;
        TextView mHeaderText;

        HeaderViewHolder(View view) {
            super(view);
            mProfilePhoneNumber = view.findViewById(R.id.profile_phone_number);
            mProfileCaretakerName = view.findViewById(R.id.profile_caretaker);
            mProfilePhoneIcon = view.findViewById(R.id.profile_phone_icon);
            mProfileLocationIcon = view.findViewById(R.id.profile_location_icon);
            mProfileCaretakerIcon = view.findViewById(R.id.profile_caretaker_icon);
            mProfileLocationText = view.findViewById(R.id.profile_location);
            mProfileLocationLabel = view.findViewById(R.id.profile_location_last_update);
            mProfileBPMMaxText = view.findViewById(R.id.profile_bpm_max);
            mProfileBPMMinText = view.findViewById(R.id.profile_bpm_min);
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
                JSONObject time = meal.getJSONObject("time");
                JSONArray mealItems = meal.getJSONArray("items");
                int hour = time.getInt("hour");
                int minute = time.getInt("minute");
                mTitle.setText(meal.getString("name"));
                mTime.setText(getTimeString(hour, minute));
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

        private String getTimeString(int hour, int minute) {
            String ampm;
            String finalTime;
            if (hour >= 1 && hour < 12 ) {
                ampm = "AM";
            } else if (hour > 12 && hour < 24) {
                hour = hour - 12;
                ampm = "PM";
            } else if (hour == 12) {
                ampm = "PM";
            } else {
                ampm = "AM";
                hour = 12;
            }

            finalTime = hour + ":" + minute + " " + ampm;
            return finalTime;
        }
    }
}
