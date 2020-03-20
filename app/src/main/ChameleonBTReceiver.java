package com.maxieds.chameleonminilivedebugger;

public class ChameleonBTReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED")){
            Log.d(TAG,"Bluetooth connect");
        }
    }
}