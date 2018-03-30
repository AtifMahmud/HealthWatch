package com.cpen391.healthwatch.caretaker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen391.healthwatch.R;

import java.util.ArrayList;
import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface PatientItemClickListener {
        void onEditClick(String patientName);
        void onProfileClick(String patientName);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<String> mDataset;
    private PatientItemClickListener mListener;
    private Context mContext;

    PatientListAdapter(Context context) {
        mDataset = new ArrayList<>();
        mContext = context;
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
            ivh.mTextView.setText(mDataset.get(position - 1));
            ivh.bind(mListener, mDataset.get(position - 1));
        }
    }

    void addPatient(String patientName) {
        mDataset.add(patientName);
        notifyItemInserted(mDataset.size());
    }

    void setOnPatientItemClickListener(PatientItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        // Plus one for the header
        return mDataset.size() + 1;
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
                mCircularImageProfile.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onProfileClick(patientName);
                    }
                });
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfilePhoneIcon;
        TextView mProfilePhoneNumber;
        TextView mTextView;
        HeaderViewHolder(View view) {
            super(view);
            mProfilePhoneIcon = view.findViewById(R.id.profile_phone_icon);
            mProfilePhoneNumber = view.findViewById(R.id.profile_phone_number);
            mTextView = view.findViewById(R.id.header);
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
}