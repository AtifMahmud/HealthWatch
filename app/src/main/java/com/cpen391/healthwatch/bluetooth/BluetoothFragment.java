package com.cpen391.healthwatch.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cpen391.healthwatch.R;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BluetoothFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class BluetoothFragment extends DialogFragment {
    ArrayAdapter<String> mDevicesAdapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View dialogView = View.inflate(getContext(), R.layout.dialog_bluetooth_devices, null);
        final TextView textView = dialogView.findViewById(R.id.bluetooth_devices_title);
        final ListView listView = dialogView.findViewById(R.id.bluetooth_devices_list);
        mDevicesAdapter = new ArrayAdapter<String>(getContext(), R.layout.dialog_bluetooth_devices_list_item, new ArrayList<String>());
        listView.setAdapter(mDevicesAdapter);
        setClickListener(listView);

        textView.setText("");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView);
        builder.setMessage("LIST OF AVAILABLE BLUETOOTH DEVICES")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name =  mDevicesAdapter.getItem(i);
                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBluetoothDevice(String name, String address) {
        if (name != null) {
            mDevicesAdapter.add(name);
            mDevicesAdapter.notifyDataSetChanged();
        }
    }
}



