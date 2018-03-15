package com.cpen391.healthwatch;

import android.app.AlertDialog;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen391.healthwatch.bluetooth.BluetoothActivity;
//import com.cpen391.healthwatch.bluetooth.BluetoothFragment;
import com.cpen391.healthwatch.bluetooth.BluetoothService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button bluetoothButton = (Button) findViewById(R.id.bluetooth);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBluetooth();
            }
        });
    }

//    public void showDialog() {
//        DialogFragment newFragment = new BluetoothFragment();
//        newFragment.show(getSupportFragmentManager(), "missiles");
//    }


    private void startBluetooth(){
        Intent bluetoothIntent = new Intent(this, BluetoothActivity.class);
        startActivity(bluetoothIntent);
    }
}
