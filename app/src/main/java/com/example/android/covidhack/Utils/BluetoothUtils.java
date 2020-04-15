package com.example.android.covidhack.Utils;

import android.bluetooth.BluetoothAdapter;

import java.util.Random;

public class BluetoothUtils {

    private String bluetoothname;
    private String uniqueid;

    public BluetoothUtils(){

    }

    public String getBluetoothname(){
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        return deviceName;
    }

    public String getUniqueid(){
        Random random=new Random();
        String uniqueid= ""+random.nextInt(100000000);
        return uniqueid;
    }

    public void setBluetoothname(String bluetoothname) {
        this.bluetoothname = bluetoothname;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

}
