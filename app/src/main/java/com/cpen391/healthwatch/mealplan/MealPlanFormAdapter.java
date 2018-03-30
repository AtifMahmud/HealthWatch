package com.cpen391.healthwatch.mealplan;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cpen391.healthwatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by william on 2018/3/27.
 * This adapter displays the items that care taker can fill in for user as part of their meal plan.
 */
public class MealPlanFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private int mItemCount;
    private String mPatientName;
    private List<String> mMealItems;

    /**
     * @param name name of the patient meal plan is for.
     *
     * Postcondition: When adding this adapter to recycler view make sure TYPE_HEADER
     * views are never recycled.
     */
    MealPlanFormAdapter(String name) {
        // 1 header item, 3 meal edit items and 1  footer item
        mItemCount = 5;
        mMealItems = new ArrayList<>();
        for (int i = 0; i < mItemCount - 2; i++) {
            mMealItems.add("");
        }
        mPatientName = name;
    }

    public String getPatientName() {
        return mPatientName;
    }

    /**
     * Checks to see if all the meal items are empty.
     * @return true if all the meal items are empty, false otherwise.
     */
    boolean hasAllEmptyMealItems() {
        for (String mealItem : mMealItems) {
            if (!mealItem.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return the list of meal items that user inputted. This ignores the meal items
     * that the user didn't input.
     */
    List<String> getMealItems() {
        List<String> mealItems = new ArrayList<>();
        for (String mealItem : mMealItems) {
            if (!mealItem.isEmpty()) {
                mealItems.add(mealItem);
            }
        }
        return mealItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meal_plan_edit_header, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meal_edit_footer, parent, false);
            FooterViewHolder fvh = new FooterViewHolder(v);
            fvh.mAddButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemCount++;
                    mMealItems.add("");
                    notifyItemInserted(mItemCount);
                }
            });
            return fvh;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meal_edit_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            hvh.bind();
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder ivh = (ItemViewHolder) holder;
            ivh.mTextInputLayout.setHint(String.format(Locale.CANADA, "Meal Item %d", position));
            ivh.mMealItemPosition = position - 1;
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == mItemCount - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout mTextInputLayout;
        EditText mEditText;
        // Meal item's position in the mMealItems list
        int mMealItemPosition;
        ItemViewHolder(View view) {
            super(view);
            mTextInputLayout = view.findViewById(R.id.input_meal_item_text_layout);
            mEditText = view.findViewById(R.id.input_meal_item);
            setListener();
        }

        private void setListener() {
            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Do nothing
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mMealItems.set(mMealItemPosition, charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    // Do nothing
                }
            });
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mNameText;
        EditText mEditText;
        TimePicker mTimePicker;
        TextView mHeaderTextView;
        HeaderViewHolder(View view) {
            super(view);
            mNameText = view.findViewById(R.id.meal_plan_patient_name);
            mEditText = view.findViewById(R.id.input_meal_name);
            mTimePicker = view.findViewById(R.id.time_picker);
            mHeaderTextView = view.findViewById(R.id.header);
        }

        void bind() {
            mNameText.setText(String.format(Locale.CANADA, "%s's", mPatientName));
            mHeaderTextView.setText(R.string.meal_edit_list_header);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        ImageView mAddButton;
        FooterViewHolder(View view) {
            super(view);
            mAddButton = view.findViewById(R.id.meal_edit_add_item);
        }
    }
}
