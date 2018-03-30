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
 * Dialog to displaying to user if they are trying to leave a fill out form with some filled out
 * fields.
 */
public class ConfirmUnsavedDialog extends DialogFragment {
    public interface OnConfirmUnsavedDialogListener {
        void onConfirmUnsavedDialogPositiveClick();
        void onConfirmUnsavedDialogNegativeClick();
    }

    private OnConfirmUnsavedDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_unsaved)
                .setPositiveButton(R.string.leave, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onConfirmUnsavedDialogPositiveClick();
                        }
                    }
                })
                .setNegativeButton(R.string.stay, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onConfirmUnsavedDialogNegativeClick();
                        }
                    }
                });
        return builder.create();
    }

    public void setOnConfirmUnsavedListener(OnConfirmUnsavedDialogListener listener) {
        mListener = listener;
    }
}
