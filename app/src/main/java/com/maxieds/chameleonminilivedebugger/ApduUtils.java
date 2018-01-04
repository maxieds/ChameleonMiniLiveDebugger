package com.maxieds.chameleonminilivedebugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxie on 1/4/18.
 */

public class ApduUtils {

    public static final int CLS = 0;
    public static final int INS = 1;

    public static List<String> parseDesfireInstructions(byte[] dataBytes) {
        List<String> insList = new ArrayList<String>();
        List<String[]> csvLines;
        try {
            csvLines = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.desfire_ins));
        } catch(IOException ioe) {
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            byte cls = Utils.hexString2Byte(csvLine[0]);
            byte ins = Utils.hexString2Byte(csvLine[1]);
            String apduLabel = csvLine[2];
            if(dataBytes.length >= 2 && dataBytes[CLS] == cls && dataBytes[INS] == ins)
                insList.add(apduLabel);
        }
        return insList;
    }

    public static List<String> parseDesfireStatusCodes(byte[] dataBytes) {
         List<String> insList = new ArrayList<String>();
        List<String[]> csvLines;
        try {
            csvLines = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.desfire_status));
        } catch(IOException ioe) {
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            byte sw1 = Utils.hexString2Byte(csvLine[0]);
            byte sw2 = Utils.hexString2Byte(csvLine[1]);
            String apduLabel = csvLine[2];
            int SW1 = dataBytes.length - 2;
            int SW2 = dataBytes.length - 1;
            if(dataBytes.length >= 2 && dataBytes[SW1] == sw1 && dataBytes[SW2] == sw2)
                insList.add(apduLabel);
        }
        return insList;
    }


}