package com.cpen391.healthwatch.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cpen391.healthwatch.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BluetoothFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

//public class BluetoothFragment extends DialogFragment {
//    StringArrayAdapter mDevicesAdapter;
//
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        final View dialogView = View.inflate(getContext(), R.layout.dialog_bluetooth_devices, null);
//        final TextView textView = dialogView.findViewById(R.id.bluetooth_devices_title);
//        final ListView listView = dialogView.findViewById(R.id.bluetooth_devices_list);
//        mDevicesAdapter = new StringArrayAdapter(getContext(), R.layout.dialog_bluetooth_devices_list_item, new ArrayList<String>());
//        listView.setAdapter(mDevicesAdapter);
//        setClickListener(listView);
//
//        textView.setText("");
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(dialogView);
//        builder.setMessage("LIST OF AVAILABLE BLUETOOTH DEVICES")
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }
//
//    private void setClickListener(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String name = mDevicesAdapter.getItem(i);
//                Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
//
//
//                String address = mDevicesAdapter.getPair(i).second;
//                BluetoothActivity bluetoothActivity = (BluetoothActivity) getActivity();
//                BluetoothDevice device = bluetoothActivity.connectAddress(address);
//                BluetoothSocket tmp = null;
//                BluetoothSocket mmSocket = null;
//
//                // Get a BluetoothSocket for a connection with the
//                // given BluetoothDevice
//                try {
//                    tmp = device.createRfcommSocketToServiceRecord();
//                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
//                    tmp = (BluetoothSocket) m.invoke(device, 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                mmSocket = tmp;
//
//            }
//        });
//    }
//
//    public void addBluetoothDevice(String name, String address) {
//        if (name != null) {
//            mDevicesAdapter.addDevice(name, address);
//            mDevicesAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private class StringArrayAdapter extends ArrayAdapter<String> {
//        List<Pair<String, String>> mPairs;
//
//        public StringArrayAdapter(Context context, int res, List<String> names) {
//            super(context, res, names);
//        }
//
//        public void addDevice(String name, String address) {
//            this.add(name);
//            mPairs.add(new Pair<String, String>(name, address));
//        }
//
//        public Pair<String, String> getPair(int i) {
//            return mPairs.get(i);
//
//        }
//    }
//}



