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

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;

public class ChameleonCommands {

    private static final String TAG = ChameleonCommands.class.getSimpleName();

    public static void cloneMFU() {
        String dumpMFUOutput = ChameleonIO.getSettingFromDevice("DUMP_MFU");
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DUMP_MFU", dumpMFUOutput));
        ChameleonIO.executeChameleonMiniCommand("CLONE", ChameleonIO.TIMEOUT);
        String cloneCmdOutput = ChameleonIO.DEVICE_RESPONSE_CODE;
        cloneCmdOutput += Arrays.asList(ChameleonIO.DEVICE_RESPONSE).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "\n");
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CLONE", cloneCmdOutput));
    }

    public static void cloneStockDumpImages(String stockChipType) {
        String chipType;
        int cardFilePath;
        if(stockChipType.equals("MFC1K_RCFK")) {
            chipType = "MF_CLASSIC_1K";
            cardFilePath = R.raw.mfc1k_random_content_fixed_keys;
        }
        else if(stockChipType.equals("MFC4K_RCFK")) {
            chipType = "MF_CLASSIC_4K";
            cardFilePath = R.raw.mfc4k_random_content_fixed_keys;
        }
        else if(stockChipType.equals("MFC1K")) {
            chipType = "MF_CLASSIC_1K";
            cardFilePath = R.raw.mifare_classic_1k;
        }
        else if(stockChipType.equals("MFU")) {
            chipType = "MF_ULTRALIGHT";
            cardFilePath = R.raw.mifare_ultralight;
        }
        else if(stockChipType.equals("DESFIRE")) {
            chipType = "MF_DESFIRE";
            cardFilePath = R.raw.mfdesfire_sample_dump;
        }
        else if(stockChipType.equals("EM4233")) {
            chipType = "EM4233";
            cardFilePath = R.raw.em4233_example;
        }
        else {
            return;
        }
        ChameleonIO.executeChameleonMiniCommand("CONFIG=" + chipType, ChameleonIO.TIMEOUT);
        ExportTools.uploadCardFromRawByXModem(cardFilePath);
        ChameleonIO.deviceStatus.startPostingStats(250);
    }

    public static void uploadCardImageByXModem() {
        // should potentially fix a slight "bug" where the card uploads but fails to get transferred to the
        // running device profile due to differences in the current configuration's memsize setting.
        // This might be more of a bug with the Chameleon software, but not entirely sure.
        // Solution: Clear out the current setting slot to CONFIG=NONE before performing the upload:
        //getSettingFromDevice(serialPort, "CONFIG=NONE");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(Uri.parse("//sdcard//Download//"), "*/*");
        try {
            LiveLoggerActivity.getLiveLoggerInstance().startActivityForResult(Intent.createChooser(intent, "Select a Card File to Upload"), ExternalFileIO.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException e) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to choose card file: " + e.getMessage()));
        }
        String cardFilePath = "";
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            cardFilePath = rte.getMessage().split("java.lang.RuntimeException: ")[1];
            Log.i(TAG, "Chosen Card File: " + cardFilePath);
        }
        ExportTools.uploadCardFileByXModem(cardFilePath);
    }

    public static void runCommand(String cmCmd) {
        if (!ChameleonIO.REVE_BOARD && (cmCmd.equals("DUMP_MFU") || cmCmd.equals("IDENTIFY") || cmCmd.equals("CLONE"))) {
            int oldTimeout = ChameleonIO.TIMEOUT;
            ChameleonIO.TIMEOUT = 5000; // extend the timeout on these long commands
            String mfuBytes = ChameleonIO.getSettingFromDevice(cmCmd);
            ChameleonIO.TIMEOUT = oldTimeout;
            ChameleonIO.DEVICE_RESPONSE[0] = Arrays.toString(ChameleonIO.DEVICE_RESPONSE);
            ChameleonIO.DEVICE_RESPONSE[0] = ChameleonIO.DEVICE_RESPONSE[0].substring(1, ChameleonIO.DEVICE_RESPONSE[0].length() - 1);
            mfuBytes = mfuBytes.replace(",", "");
            mfuBytes = mfuBytes.replace("\n", "");
            mfuBytes = mfuBytes.replace("\r", "");
            if (cmCmd.equals("DUMP_MFU")) {
                String mfuBytesPrettyPrint = Utils.prettyPrintMFU(mfuBytes);
                MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DUMP_MFU", mfuBytesPrettyPrint));
            } else
                MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(cmCmd, mfuBytes));
        } else {
            String rdata = ChameleonIO.getSettingFromDevice(cmCmd);
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(cmCmd, rdata));
        }
    }

    public static void createNewCommandEvent(String createCmd) {
        String msgParam = "";
        if(createCmd.equals("LIST CONFIG")) {
            if(!ChameleonIO.REVE_BOARD)
                msgParam = ChameleonIO.getSettingFromDevice("CONFIG=?");
            else
                msgParam = ChameleonIO.getSettingFromDevice("configmy");
            msgParam = " => " + msgParam;
            msgParam = msgParam.replaceAll(",", "\n => ");
            createCmd = "CONFIG?";
        }
        else if(createCmd.equals("RESET") || createCmd.equals("resetmy")) { // need to re-establish the usb connection:
            ChameleonIO.executeChameleonMiniCommand(createCmd, ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
            ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
            if(serialIOPort != null) {
                serialIOPort.shutdownSerial();
                serialIOPort.configureSerial();
            }
            msgParam = "Reconfigured the Chameleon USB settings.";
        }
        else if(createCmd.equals("RANDOM UID")) {
            ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
            String uidCmd = ChameleonIO.REVE_BOARD ? "uid=" : "UID=";
            byte[] randomBytes = Utils.getRandomBytes(ChameleonIO.deviceStatus.UIDSIZE);
            String sendCmd = uidCmd + Utils.bytes2Hex(randomBytes).replace(" ", "").toUpperCase();
            ChameleonIO.getSettingFromDevice(sendCmd);
            msgParam = "Next UID set to " + Utils.bytes2Hex(randomBytes).replace(" ", ":").toUpperCase();
        }
        else if(createCmd.equals("Log Replay")) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", "RE: LOG REPLAY: This is a wishlist feature. It might be necessary to add it to the firmware and implement it in hardware. Not currently implemented."));
            return;
        }
        else if(createCmd.equals("STATUS") || createCmd.equals("SET ACTIVITY") ||
                createCmd.equals("ON FOOT") || createCmd.equals("LOCATION") ||
                createCmd.equals("CARD INFO") || createCmd.equals("DOOR") ||
                createCmd.equals("VENDING") || createCmd.equals("HOME") ||
                createCmd.equals("WORK") || createCmd.equals(" DEVICE PROFILE") ||
                createCmd.equals("TODO LIST")) {
            try {
                MainActivityLogUtils.displayUserInputPrompt("Description of the new event? ");
                Looper.loop();
            }
            catch(RuntimeException msgReady) {}
            msgParam = MainActivityLogUtils.userInputStack;
            MainActivityLogUtils.userInputStack = null;
        }
        else if(createCmd.equals("ONCLICK")) {
            msgParam = "SYSTICK Millis := " + ChameleonIO.getSettingFromDevice("SYSTICK?");
        }
        else if(createCmd.equals("GETUID")) {
            String queryCmd = ChameleonIO.REVE_BOARD ? "uid?" : "GETUID";
            String rParam = ChameleonIO.getSettingFromDevice(queryCmd);
            msgParam = "GETUID: " + rParam;
        }
        else if(createCmd.equals("AUTOCAL")) {
            msgParam = ChameleonIO.getSettingFromDevice("AUTOCALIBRATE");
        }
        else {
            msgParam = ChameleonIO.getSettingFromDevice(createCmd);
        }
        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
    }
}
