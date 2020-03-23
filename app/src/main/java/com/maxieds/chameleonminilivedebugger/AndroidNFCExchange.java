package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class AndroidNFCExchange {

    private static final String TAG = AndroidNFCExchange.class.getSimpleName();

    public static boolean isNFCEnabled() {
        NfcManager nfcManager = (NfcManager) LiveLoggerActivity.getInstance().getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public static boolean isNFCAvailable() {
        NfcManager nfcManager = (NfcManager) LiveLoggerActivity.getInstance().getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        return nfcAdapter != null;
    }

    public static void displayAndroidNFCSettings() {
        if(!isNFCAvailable()) {
            Utils.displayToastMessageShort("NFC is not available on this Android device.");
            return;
        }
        Intent intentOpenNFCSettings = new Intent();
        intentOpenNFCSettings.setAction(android.provider.Settings.ACTION_NFC_SETTINGS);
        LiveLoggerActivity.getInstance().startActivity(intentOpenNFCSettings);
    }

}
