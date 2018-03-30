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

/**
 * Created by william on 2018/3/29.
 *
 */

public class PatientProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int TYPE_HEADER = 0;
    private Context mContext;

    PatientProfileAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.caretaker_page_header, parent, false);
            return new HeaderViewHolder(v);
        }
        throw new IllegalArgumentException("Other views doesn't exist");
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_HEADER;
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
}
