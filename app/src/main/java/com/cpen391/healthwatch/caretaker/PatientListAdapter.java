package com.cpen391.healthwatch.caretaker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen391.healthwatch.R;

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface PatientItemClickListener {
        void onEditClick(String patientName);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String[] mDataset;
    private PatientItemClickListener mListener;

    PatientListAdapter(String[] dataset) {
        mDataset = dataset;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.caretaker_page_header, parent, false);
            return new HeaderViewHolder(v);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patient_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).mTextView.setText(R.string.patients_list_header);
        } else {
            // Since first position is reserved for header the position of the item is offset by one
            ItemViewHolder ivh = (ItemViewHolder) holder;
            ivh.mTextView.setText(mDataset[position - 1]);
            ivh.bind(mListener, mDataset[position - 1]);
        }
    }

    void setOnPatientItemClickListener(PatientItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        // Plus one for the header
        return mDataset.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mCircularImageProfile;
        ImageView mMealEditImageView;

        ItemViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.patient_name);
            mCircularImageProfile = view.findViewById(R.id.profile_image);
            mMealEditImageView = view.findViewById(R.id.patient_meal_plan_edit);
        }

        void bind(final PatientItemClickListener listener, final String patientName) {
            if (listener != null) {
                mMealEditImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onEditClick(patientName);
                    }
                });
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        HeaderViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.header);
        }
    }
}