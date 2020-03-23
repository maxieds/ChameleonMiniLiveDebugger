package com.maxieds.chameleonminilivedebugger;

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    public static boolean allowWiredUSB = true;
    public static boolean allowBluetooth = false;
    public static boolean allowAndroidNFC = false;
    public static int serialBaudRate = 115200;

    public static String chameleonDeviceSerialNumber = "<UNKNOWN>";
    public static String chameleonDeviceNickname = "Chameleon Mini (Default)";

    public static final int SNIFFING_MODE_UNIDIRECTIONAL = 1;
    public static final int SNIFFING_MODE_BIDIRECTIONAL = 2;
    public static int sniffingMode = SNIFFING_MODE_BIDIRECTIONAL;

    public static final int USBIO_IFACE_INDEX = 0;
    public static final int BTIO_IFACE_INDEX = 1;
    public static ChameleonSerialIOInterface[] serialIOPorts = new ChameleonSerialIOInterface[2];
    static {
        serialIOPorts[USBIO_IFACE_INDEX] = new SerialUSBInterface(LiveLoggerActivity.getInstance());
        serialIOPorts[BTIO_IFACE_INDEX] = new BluetoothSerialInterface(LiveLoggerActivity.getInstance());
    };
    public static int SERIALIO_IFACE_ACTIVE_INDEX = -1;

    public static ChameleonSerialIOInterface getActiveSerialIOPort() {
        if(SERIALIO_IFACE_ACTIVE_INDEX < 0) {
            return null;
        }
        else if(!serialIOPorts[SERIALIO_IFACE_ACTIVE_INDEX].serialConfigured()) {
            return null;
        }
        return serialIOPorts[SERIALIO_IFACE_ACTIVE_INDEX];
    }

    public static void initializeSerialIOConnections() {
        for(int si = 0; si < serialIOPorts.length; si++) {
            if((si == USBIO_IFACE_INDEX && allowWiredUSB) ||
                    (allowBluetooth && ((BluetoothSerialInterface) serialIOPorts[si]).isBluetoothEnabled())) {
                serialIOPorts[si].startScanningDevices();
            }
        }
    }

    public static void stopSerialIOConnectionDiscovery() {
        for(int si = 0; si < serialIOPorts.length; si++) {
            serialIOPorts[si].stopScanningDevices();
        }
    }


}
