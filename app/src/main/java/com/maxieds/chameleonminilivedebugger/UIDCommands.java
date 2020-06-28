package com.maxieds.chameleonminilivedebugger;

import java.util.Locale;

public class UIDCommands {

    private static final String TAG = UIDCommands.class.getSimpleName();

    public static byte[] processUIDCommand(String uidAction) {
        String uidStr = ChameleonIO.deviceStatus.UID;
        uidStr.replace(":", "");
        byte[] uid = Utils.hexString2Bytes(uidStr);
        int uidSize = uid.length - 1;
        if(uidAction.equals("INCREMENT_RIGHT"))
            uid[uidSize] += (byte) 0x01;
        else if(uidAction.equals("DECREMENT_RIGHT"))
            uid[uidSize] -= (byte) 0x01;
        else if(uidAction.equals("SHIFT_RIGHT")) {
            byte[] nextUID = new byte[uid.length];
            System.arraycopy(uid, 1, nextUID, 0, uidSize);
            uid = nextUID;
        }
        else if(uidAction.equals("INCREMENT_LEFT"))
            uid[0] += (byte) 0x01;
        else if(uidAction.equals("DECREMENT_LEFT"))
            uid[0] -= (byte) 0x100;
        else if(uidAction.equals("SHIFT_LEFT")){
            byte[] nextUID = new byte[uid.length];
            System.arraycopy(uid, 0, nextUID, 1, uidSize);
            uid = nextUID;
        }
        else if(uidAction.equals("LAST_UID")) {
            uid = Utils.hexString2Bytes(ChameleonIO.deviceStatus.LASTUID);
            ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
        }
        return uid;
    }

    public static void getBitsHelper(String action) {
        String dataBytesStr = new String();
        if(action.equals("UID") && Settings.getActiveSerialIOPort() != null) {
            dataBytesStr = ChameleonIO.deviceStatus.UID;
        }
        else if(action.equals("RANDOM")) {
            if(ChameleonIO.deviceStatus.UIDSIZE == 0)
                dataBytesStr = Utils.bytes2Hex(Utils.getRandomBytes(7));
            else
                dataBytesStr = Utils.bytes2Hex(Utils.getRandomBytes(ChameleonIO.deviceStatus.UIDSIZE));
        }
        ApduUtils.apduTransceiveCmd.setPayloadData(dataBytesStr);
        ApduUtils.updateAssembledAPDUCmd();
    }

    public static void modifyUID(String uidAction) {
        if(ChameleonIO.deviceStatus.UID == null || ChameleonIO.deviceStatus.UID.equals("DEVICE UID") || ChameleonIO.deviceStatus.UID.equals("NO UID."))
            return;
        ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
        byte[] uid = UIDCommands.processUIDCommand(uidAction);
        String uidCmd = ChameleonIO.REVE_BOARD ? "uid" : "UID";
        String cmdStatus = ChameleonIO.getSettingFromDevice(String.format(Locale.ENGLISH, "%s=%s", uidCmd, Utils.bytes2Hex(uid).replace(" ", "").toUpperCase()));
        ChameleonIO.deviceStatus.startPostingStats(250);
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("UID", "Next device UID set to " + Utils.bytes2Hex(uid).replace(" ", ":").toUpperCase()));
    }

}
