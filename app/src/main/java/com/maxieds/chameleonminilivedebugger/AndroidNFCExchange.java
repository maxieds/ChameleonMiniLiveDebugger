/*
This program (The Chameleon Mini Live Debugger) is free software written by
Maxie Dion Schmidt: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The complete license provided with source distributions of this library is
available at the following link:
https://github.com/maxieds/ChameleonMiniLiveDebugger
*/

package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class AndroidNFCExchange {

    private static final String TAG = AndroidNFCExchange.class.getSimpleName();

    public static boolean isNFCEnabled() {
        NfcManager nfcManager = (NfcManager) LiveLoggerActivity.getLiveLoggerInstance().getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public static boolean isNFCAvailable() {
        NfcManager nfcManager = (NfcManager) LiveLoggerActivity.getLiveLoggerInstance().getSystemService(Context.NFC_SERVICE);
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
        LiveLoggerActivity.getLiveLoggerInstance().startActivity(intentOpenNFCSettings);
    }

}
