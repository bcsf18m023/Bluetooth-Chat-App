package com.example.bluetooth_chat_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {
    private ListView listPairedDevices,listAvailableDevices;
    private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private ProgressBar progressScanDevices;
    private BroadcastReceiver bluetoothDeviceListener=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()!=BluetoothDevice.BOND_BONDED)
                {
                    adapterAvailableDevices.add(device.getName()+"\n"+device.getAddress());
                }

            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                progressScanDevices.setVisibility(View.GONE);
                if (adapterAvailableDevices.getCount()==0)
                {
                    Toast.makeText(context,"No New Devices Found",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context,"Click On Device to Start Chat",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context=this;
        init();
    }
    private void init(){

        progressScanDevices = findViewById(R.id.menu_scan_devices);

        listPairedDevices=findViewById(R.id.list_paired_devices);
        listAvailableDevices=findViewById(R.id.list_available_devices);

        adapterPairedDevices=new ArrayAdapter<String>(context,R.layout.device_list_item);
        adapterAvailableDevices=new ArrayAdapter<String>(context,R.layout.device_list_item);

        listPairedDevices.setAdapter(adapterPairedDevices);
        listAvailableDevices.setAdapter(adapterAvailableDevices);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices=bluetoothAdapter.getBondedDevices();

        if(pairedDevices!=null && pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                adapterPairedDevices.add(device.getName()+"\n"+device.getAddress());
            }
        }


        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener,intentFilter);
        IntentFilter intentFilter1=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener,intentFilter1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_scan_devices:
                scanDevices();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void scanDevices(){

        progressScanDevices.setVisibility(View.VISIBLE);
        adapterAvailableDevices.clear();
        Toast.makeText(context,"Scan Started",Toast.LENGTH_SHORT).show();


        if(bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

    }
}