package com.cpen391.healthwatch.mealplan;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

import com.cpen391.healthwatch.R;

/**
 * Created by william on 2018/3/28.
 * This dialog shows a confirmation message for a form to the user.
 */
public class ConfirmFieldsDialog extends DialogFragment {
    public interface OnConfirmFieldsDialogListener {
        void onConfirmFieldsDialogPositiveClick();
        void onConfirmFieldsDialogNegativeClick();
    }

    private OnConfirmFieldsDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_fields)
                .setPositiveButton(R.string.confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onConfirmFieldsDialogPositiveClick();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onConfirmFieldsDialogNegativeClick();
                        }
                    }
                });
        return builder.create();
    }

    public void setListener(OnConfirmFieldsDialogListener listener) {
        mListener = listener;
    }
}
