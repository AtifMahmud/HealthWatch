package com.cpen391.healthwatch.bluetooth;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.cpen391.healthwatch.R;

/**
 * Created by william on 2018/3/21.
 * Dialog to ask user whether or not they want to use bluetooth and pair with
 * a healthwatch device.
 */
public class BluetoothDialog extends DialogFragment {
    public interface OnClickDialogListener {
        void onPositiveClick();
        void onNegativeClick();
    }

    private OnClickDialogListener mListener;

    public void setListener(OnClickDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new Builder(getContext());
        builder.setMessage(R.string.dialog_use_bluetooth)
                .setPositiveButton(R.string.confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onPositiveClick();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onNegativeClick();
                        }
                    }
                });
        return builder.create();
    }
}
