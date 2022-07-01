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

import android.view.View;
import android.widget.TextView;

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
    public static final int CLSIDX = 0;
    public static final int INSIDX = 1;

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
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return insList;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            byte cls = Utils.hexString2Byte(csvLine[0]);
            byte ins = Utils.hexString2Byte(csvLine[1]);
            String apduLabel = csvLine[2];
            if(dataBytes.length >= 2 && dataBytes[CLSIDX] == cls && dataBytes[INSIDX] == ins)
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
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return insList;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
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
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return insList;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
            return insList;
        }
        for(int i = 0; i < csvLines.size(); i++) {
            String[] csvLine = csvLines.get(i);
            byte ins = Utils.hexString2Byte(csvLine[0]);
            String apduLabel = csvLine[1];
            if(dataBytes.length >= 2 && dataBytes[INSIDX] == ins || ((dataBytes.length == 1 || dataBytes.length == 2) && dataBytes[CLSIDX] == ins))
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
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return insList;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
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
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return insList;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
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
     * @ref ApduUtils.parseDesfireInstructions
     * @ref ApduUtils.parseDesfireStatusCodes
     * @ref ApduUtils.parseCommonInstructions
     * @ref ApduUtils.parseStatusCodes
     * @ref ApduUtils.parseDetailedInstructions
     * @ref https://www.dropbox.com/s/bqrd6jzemwo4ux0/isoiec7816-4%7Bed2.0%7Den.pdf?dl=0
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

    public static class APDUCommandData implements Comparable<APDUCommandData> {

        public String CLA;
        public String INS;
        public String P1, P2;
        private String LE, LC;
        private String payloadData;
        public String apduCmdDesc;

        public APDUCommandData() {
            clear();
        }

        public void computeLELCBytes() {
            LE = String.format("%02x", payloadData.length() / 2);
            LC = (payloadData.length() == 0) ? "" : "000000";
        }

        public void setPayloadData(String byteString) {
            payloadData = byteString.replaceAll("[ \n\r\t:.]*", "");
            if(payloadData.length() % 2 == 1)
                payloadData = "0" + payloadData;
        }

        public static final String APDU_PAYLOAD_EXPORT_TYPE_DEFAULT_WRAPPED = "DEFAULT_WRAPPED";
        public static final String APDU_PAYLOAD_EXPORT_TYPE_NATIVE_NOWRAP = "NATIVE_NOWRAP";
        public static final String APDU_PAYLOAD_EXPORT_TYPE_NOWRAP_WITH_CRC = "NOWRAP_WITH_CRC";

        public String getPayloadData(String apduExportType) {
            assembleAPDUString(apduExportType);
            return payloadData;
        }

        public byte[] assembleAPDU(String apduExportType) {
            if(payloadData.length() == 0) {
                LE = "00";
                LC = "";
            }
            else {
                LE = String.format("%02x", payloadData.length() / 2);
                LC = "000000";
            }
            String apduRawBytes = "";
            boolean appendCRCBytes = true;
            switch(apduExportType) {
                case APDU_PAYLOAD_EXPORT_TYPE_NATIVE_NOWRAP:
                    apduRawBytes = CLA + INS + payloadData;
                    appendCRCBytes = false;
                    break;
                case APDU_PAYLOAD_EXPORT_TYPE_NOWRAP_WITH_CRC:
                    apduRawBytes = CLA + INS + payloadData;
                    break;
                case APDU_PAYLOAD_EXPORT_TYPE_DEFAULT_WRAPPED:
                default:
                    apduRawBytes = CLA + INS + P1 + P2 + LE + payloadData + LC;
                    break;
            }
            if(appendCRCBytes) {
                byte[] rawBytes = Utils.hexString2Bytes(apduRawBytes);
                String crcByteStr = Utils.bytes2Hex(Utils.calculateByteBufferCRC16(rawBytes));
                apduRawBytes += crcByteStr;
            }
            return Utils.hexString2Bytes(apduRawBytes.replaceAll("x", "0")); // zero fill the x-marker bits
        }

        public byte[] assembleAPDU() {
            return assembleAPDU("");
        }

        public String assembleAPDUString(String apduExportType) {
            return Utils.bytes2Hex(assembleAPDU(apduExportType));
        }

        public String assembleAPDUString() {
            return assembleAPDUString("");
        }

        public void clear() {
            CLA = INS = P1 = P2 = LE = LC = "xx";
            apduCmdDesc = payloadData = "";
        }

        public void loadFromStringArray(String[] apduSpec) {
            if(apduSpec.length == 2) {
                INS = apduSpec[0];
                apduCmdDesc = apduSpec[1];
            }
            else if(apduSpec.length == 3) {
                CLA = apduSpec[0];
                INS = apduSpec[1];
                apduCmdDesc = apduSpec[2];
            }
            else if(apduSpec.length == 4) {
                CLA = apduSpec[0];
                INS = apduSpec[1];
                P1 = apduSpec[2];
                apduCmdDesc = apduSpec[3];
            }
            else if(apduSpec.length == 5) {
                CLA = apduSpec[0];
                INS = apduSpec[1];
                P1 = apduSpec[2];
                P2 = apduSpec[3];
                apduCmdDesc = apduSpec[4];
            }
            else if(apduSpec.length == 6) {
                CLA = apduSpec[0];
                INS = apduSpec[1];
                P1 = apduSpec[2];
                P2 = apduSpec[3];
                LE = apduSpec[4];
                apduCmdDesc = apduSpec[5];
            }
        }

        @Override
        public int compareTo(APDUCommandData apdu) {
            return apdu.apduCmdDesc.compareTo(apduCmdDesc);
        }

        public String getSummary() {
            int numRHSSpaces = 45 - apduCmdDesc.length();
            String formatResp = "%s" + String.format("%" + numRHSSpaces + "s", "") + " : %s %s %s %s %s";
            String sstr = String.format(formatResp, apduCmdDesc, CLA, INS, P1, P2, LE);
            return sstr;
        }

    }

    public static APDUCommandData apduTransceiveCmd = new APDUCommandData();
    public static APDUCommandData[] fullInsList;
    public static String[] fullInsDescList;
    public static View tabView;

    public static void buildFullInstructionsList() {
        List<String[]> apduStringFormattedSpecs = new ArrayList();
        try {
            apduStringFormattedSpecs = Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.desfire_ins));
            apduStringFormattedSpecs.addAll(Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.common_ins)));
            apduStringFormattedSpecs.addAll(Utils.readCSVFile(LiveLoggerActivity.defaultContext.getResources().openRawResource(R.raw.detailed_common_ins)));
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
            return;
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            fullInsList = null;
            return;
        }
        fullInsList = new APDUCommandData[apduStringFormattedSpecs.size()];
        fullInsDescList = new String[apduStringFormattedSpecs.size()];
        for(int a = 0; a < apduStringFormattedSpecs.size(); a++) {
            fullInsList[a] = new APDUCommandData();
            fullInsList[a].loadFromStringArray(apduStringFormattedSpecs.get(a));
            fullInsDescList[a] = fullInsList[a].apduCmdDesc;
        }
    }

    public static void updateAssembledAPDUCmd() {
        try {
            ((TextView) tabView.findViewById(R.id.apduCLA)).setText(apduTransceiveCmd.CLA);
            ((TextView) tabView.findViewById(R.id.apduINS)).setText(apduTransceiveCmd.INS);
            ((TextView) tabView.findViewById(R.id.apduP1)).setText(apduTransceiveCmd.P1);
            ((TextView) tabView.findViewById(R.id.apduP2)).setText(apduTransceiveCmd.P2);
            String payloadDataText = apduTransceiveCmd.payloadData.equals("") ? "NONE" : apduTransceiveCmd.payloadData;
            ((TextView) tabView.findViewById(R.id.apduPayloadData)).setText(payloadDataText);
        } catch(NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
        }
    }

}