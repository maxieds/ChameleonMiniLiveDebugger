package com.maxieds.chameleonminilivedebugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>APDU Utilities</h1>
 * The ApduUtils class provides several utilities for recognizing
 * and parsing APDU commands in returned Logs and commands
 *
 * @author  Maxie D. Schmidt
 * @since   1/4/18
 */
public class ApduUtils {

    /**
     * Constants for the respective indices of the CLS and INS entries in a standard APDU command.
     */
    public static final int CLS = 0;
    public static final int INS = 1;

    /**
     * This method attempts to recognize Desfire-specific instructions in the logged payload bytes.
     * @param dataBytes
     * @return List<String> of recognized instructions
     * @see res/raw/desfire_ins
     */
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

    /**
     * This method attempts to recognize Desfire-specific status codes in the logged payload bytes.
     * @param dataBytes
     * @return List<String> of recognized instructions
     * @see res/raw/desfire_status
     */
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

    /**
     * This method attempts to recognize common (mostly ISO) APDU instructions in the logged payload bytes.
     * @param dataBytes
     * @return List<String> of recognized instructions
     * @see res/raw/common_ins
     */
    public static List<String> parseCommonInstructions(byte[] dataBytes) {
        List<String> insList = new ArrayList<String>();
        List<String[]> csvLines;
        try {
            csvLines = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.common_ins));
        } catch(IOException ioe) {
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            byte ins = Utils.hexString2Byte(csvLine[0]);
            String apduLabel = csvLine[1];
            if(dataBytes.length >= 2 && dataBytes[INS] == ins || ((dataBytes.length == 1 || dataBytes.length == 2) && dataBytes[CLS] == ins))
                insList.add(apduLabel);
        }
        return insList;
    }

    /**
     * This method attempts to recognize common (mostly ISO) APDU instructions in the logged payload bytes.
     * @param dataBytes
     * @return List<String> of recognized instructions
     * @see res/raw/detailed_common_ins
     */
    public static List<String> parseDetailedInstructions(byte[] dataBytes) {
        List<String> insList = new ArrayList<String>();
        List<String[]> csvLines;
        try {
            csvLines = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.detailed_common_ins));
        } catch(IOException ioe) {
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            int checkNumBytes = csvLine.length - 1;
            String apduLabel = csvLine[checkNumBytes];
            if(dataBytes.length < checkNumBytes) {
                continue;
            }
            boolean tagApplies = true;
            for(int b = 0; b < checkNumBytes; b++) {
                if(!csvLine[b].equals("xx") && dataBytes[b] != Utils.hexString2Byte(csvLine[b])) {
                    tagApplies = false;
                    break;
                }
            }
            if(tagApplies)
                insList.add(apduLabel);
        }
        return insList;

    }

    /**
     * This method attempts to recognize common APDU status codes in the logged payload bytes.
     * @param dataBytes
     * @return List<String> of recognized instructions
     * @see res/raw/response_codes_status
     */
    public static List<String> parseStatusCodes(byte[] dataBytes) {
        List<String> insList = new ArrayList<String>();
        List<String[]> csvLines;
        try {
            csvLines = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.response_codes_status));
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

    /**
     * Attempts to classify log payload data as APDU commands (instructions and status codes).
     * @param dataBytes
     * @return String list of recognized APDU commands
     * @see ApduUtils.parseDesfireInstructions
     * @see ApduUtils.parseDesfireStatusCodes
     * @see ApduUtils.parseCommonInstructions
     * @see ApduUtils.parseStatusCodes
     * @see ApduUtils.parseDetailedInstructions
     */
    public static String classifyApdu(byte[] dataBytes) {
        List<String> apduClassifications = parseDesfireInstructions(dataBytes);
        apduClassifications.addAll(parseDesfireStatusCodes(dataBytes));
        apduClassifications.addAll(parseCommonInstructions(dataBytes));
        apduClassifications.addAll(parseStatusCodes(dataBytes));
        apduClassifications.addAll(parseDetailedInstructions(dataBytes));
        StringBuilder sbApduList = new StringBuilder();
        for(String apdu : apduClassifications){
            sbApduList.append(apdu);
            sbApduList.append(", ");
        }
        String apduList = sbApduList.toString();
        if(apduList.length() >= 2)
             return apduList.substring(0, apduList.length() - 2);
        else
            return "NONE";
    }

}