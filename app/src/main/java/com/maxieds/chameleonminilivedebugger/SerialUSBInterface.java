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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Parcelable;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class SerialUSBInterface extends SerialIOReceiver {

    private static final String TAG = SerialUSBInterface.class.getSimpleName();

    public static final int USB_DATA_BITS = 8;

    public String getInterfaceLoggingTag() {
        return TAG;
    }

    private UsbSerialDevice serialPort;
    private UsbDevice activeDevice;
    private UsbSerialInterface.UsbReadCallback serialReaderCallback;
    private int baudRate;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private boolean scanning;
    private Semaphore serialPortLock = new Semaphore(1, true);

    public SerialUSBInterface(Context appContext) {
        setListenerContext(appContext);
        serialPort = null;
        baudRate = ChameleonSettings.serialBaudRate;
        serialConfigured = false;
        receiversRegistered = false;
        scanning = false;
    }

    public boolean isWiredUSB() { return true; }

    public boolean isBluetooth() { return false; }

    public int setSerialBaudRate(int bdRate) {
        baudRate = bdRate;
        ChameleonSettings.serialBaudRate = baudRate;
        if(serialPort != null) {
            serialPort.setBaudRate(baudRate);
            return baudRate;
        }
        return STATUS_OK;
    }

    private final Handler scanDeviceHandler = new Handler();
    private final Runnable scanDeviceRunnable = new Runnable() {
        public void run() {
            if (ChameleonSettings.getActiveSerialIOPort() != null || configureSerial() != 0) {
                scanDeviceHandler.removeCallbacksAndMessages(this);
            } else {
                scanDeviceHandler.removeCallbacksAndMessages(this);
                scanDeviceHandler.postDelayed(this, SCAN_POST_TIME_DELAY);
            }
        }
    };
    private static final int SCAN_POST_TIME_DELAY = 750;

    public boolean startScanningDevices() {
        if (scanning) {
            return false;
        }
        scanning = true;
        scanDeviceHandler.post(scanDeviceRunnable);
        return true;
    }

    public boolean stopScanningDevices() {
        scanning = false;
        scanDeviceHandler.removeCallbacksAndMessages(scanDeviceRunnable);
        return true;
    }

    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "";
        }
        String devInfo = String.format(BuildConfig.DEFAULT_LOCALE, "Manufacturer:  %s\nProduct Name:  %s\nVersion:       %s\nDevice Serial: %s\nUSB Dev:       %s",
                                       activeDevice.getManufacturerName(), activeDevice.getProductName(),
                                       activeDevice.getVersion(), activeDevice.getSerialNumber(),
                                       activeDevice.getDeviceName());
        return devInfo;
    }

    public int configureSerial() {
        if(serialConfigured()) {
            return STATUS_TRUE;
        }
        UsbManager usbManager = (UsbManager) LiveLoggerActivity.getLiveLoggerInstance().getSystemService(Context.USB_SERVICE);
        UsbDevice device = null;
        UsbDeviceConnection connection = null;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if(usbDevices != null && !usbDevices.isEmpty()) {
            for(Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                if(device == null)
                    continue;
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if(!usbManager.hasPermission(device)) {
                    return STATUS_FALSE;
                }
                if(deviceVID == ChameleonIO.CMUSB_VENDORID && devicePID == ChameleonIO.CMUSB_PRODUCTID) {
                    ChameleonIO.REVE_BOARD = false;
                    ChameleonIO.CHAMELEON_DEVICE_USBVID = deviceVID;
                    ChameleonIO.CHAMELEON_DEVICE_USBPID = devicePID;
                    connection = usbManager.openDevice(device);
                    break;
                }
                else if(deviceVID == ChameleonIO.CMUSB_REVE_VENDORID && devicePID == ChameleonIO.CMUSB_REVE_PRODUCTID) {
                    ChameleonIO.REVE_BOARD = true;
                    ChameleonIO.CHAMELEON_DEVICE_USBVID = deviceVID;
                    ChameleonIO.CHAMELEON_DEVICE_USBPID = devicePID;
                    connection = usbManager.openDevice(device);
                    break;
                }
            }
        }
        if(device == null || connection == null) {
            serialPort = null;
            return STATUS_ERROR;
        }
        AndroidLogger.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "USB device attached with name %s and product name %d and manu ID %s", device.getProductName(), device.getProductName(), device.getManufacturerName()));
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialPort != null && serialPort.open()) {
            serialPort.setBaudRate(ChameleonSettings.serialBaudRate);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setDataBits(USB_DATA_BITS);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialReaderCallback = createSerialReaderCallback();
            serialPort.read(serialReaderCallback);
        }
        else {
            notifyStatus("USB ERROR: ", "Unable to configure serial device.");
            serialPort = null;
            return STATUS_ERROR;
        }
        activeDevice = device;
        ChameleonSettings.chameleonDeviceSerialNumber = String.format(BuildConfig.DEFAULT_LOCALE, "%s-%s-%s",
                activeDevice.getProductName(), activeDevice.getVersion(), activeDevice.getSerialNumber());
        ChameleonIO.PAUSED = false;
        serialConfigured = true;
        receiversRegistered = true;
        ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = ChameleonSettings.USBIO_IFACE_INDEX;
        LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusIconUSB, R.drawable.usbconnected16);
        UITabUtils.updateConfigTabConnDeviceInfo(false);
        notifyStatus("USB STATUS: ", "Chameleon:     " + getActiveDeviceInfo());
        return STATUS_TRUE;
    }

    public int shutdownSerial() {
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        if(serialPort != null) {
            serialPort.close();
        }
        ChameleonIO.PAUSED = true;
        ExportTools.EOT = true;
        ExportTools.transmissionErrorOccurred = true;
        ChameleonIO.DOWNLOAD = false;
        ChameleonIO.UPLOAD = false;
        ChameleonIO.WAITING_FOR_XMODEM = false;
        ChameleonIO.WAITING_FOR_RESPONSE = false;
        ChameleonIO.EXPECTING_BINARY_DATA = false;
        ChameleonIO.LASTCMD = "";
        ChameleonIO.APPEND_PRIOR_BUFFER_DATA = false;
        ChameleonIO.PRIOR_BUFFER_DATA = new byte[0];
        serialPort = null;
        activeDevice = null;
        serialConfigured = false;
        receiversRegistered = false;
        if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.USBIO_IFACE_INDEX) {
            ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
        }
        serialPortLock.release();
        LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconUSB);
        UITabUtils.updateConfigTabConnDeviceInfo(true);
        notifyDeviceConnectionTerminated();
        return STATUS_TRUE;
    }

    private UsbSerialInterface.UsbReadCallback createSerialReaderCallback() {
        return new UsbSerialInterface.UsbReadCallback() {
            @Override
            public void onReceivedData(byte[] liveLogData) {
                if(ChameleonSettings.serialIOPorts != null && ChameleonSettings.serialIOPorts[ChameleonSettings.USBIO_IFACE_INDEX] != null) {
                    ChameleonSettings.serialIOPorts[ChameleonSettings.USBIO_IFACE_INDEX].onReceivedData(liveLogData);
                }
            }
        };
    }

    public boolean serialConfigured() { return serialConfigured; }

    public boolean serialReceiversRegistered() { return receiversRegistered; }

    public boolean acquireSerialPort() {
        try {
            serialPortLock.acquire();
            return true;
        } catch(Exception inte) {
            AndroidLogger.printStackTrace(inte);
            serialPortLock.release();
            return false;
        }
    }

    public boolean acquireSerialPortNoInterrupt() {
        try {
            serialPortLock.acquireUninterruptibly();
            return true;
        } catch(Exception inte) {
            AndroidLogger.printStackTrace(inte);
            serialPortLock.release();
            return false;
        }
    }

    public boolean tryAcquireSerialPort(int timeout) {
        boolean status = false;
        try {
            status = serialPortLock.tryAcquire(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
            return status;
        } catch(Exception inte) {
            AndroidLogger.printStackTrace(inte);
            serialPortLock.release();
            return false;
        }
    }

    public boolean releaseSerialPortLock() {
        serialPortLock.release();
        return true;
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        if(dataWriteBuffer == null || dataWriteBuffer.length == 0) {
            return STATUS_OK;
        } else if(!serialConfigured() || serialPort == null) {
            return STATUS_ERROR;
        }
        AndroidLogger.d(TAG, "USBReaderCallback Send Data: (HEX) " + Utils.bytes2Hex(dataWriteBuffer));
        AndroidLogger.d(TAG, "USBReaderCallback Send Data: (TXT) " + Utils.bytes2Ascii(dataWriteBuffer));
        serialPort.write(dataWriteBuffer);
        return STATUS_TRUE;
    }

    /* Android 10 upgrades break the prior permissions scheme for USB devices ... */
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static boolean usbPermissionsReceiverConfig = false;
    public static boolean usbPermissionsGranted = false;
    public static IntentFilter usbPermsFilter = new IntentFilter(SerialUSBInterface.ACTION_USB_PERMISSION);
    public static final BroadcastReceiver usbPermissionsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equals(ACTION_USB_PERMISSION)) {
                synchronized (this) {
                    SerialUSBInterface.usbPermissionsGranted = true;
                    if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        UsbDevice usbDev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if(usbDev != null) {
                            AndroidLogger.d(TAG, "Permission denied for USB device " + usbDev);
                        } else {
                            AndroidLogger.d(TAG, "Permission denied for NULL USB device ");
                        }
                    }
                }
            }
        }
    };

    public static void registerUSBPermission(Intent intent, Context context) {
        if(SerialUSBInterface.usbPermissionsGranted) {
            return;
        }
        if(!usbPermissionsReceiverConfig) {
            context.registerReceiver(SerialUSBInterface.usbPermissionsReceiver, usbPermsFilter);
            SerialUSBInterface.usbPermissionsReceiverConfig = true;
        }
        Intent broadcastIntent = new Intent(SerialUSBInterface.ACTION_USB_PERMISSION);
        if(intent != null) {
            Parcelable usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(usbDevice != null) {
                broadcastIntent.putExtra(UsbManager.EXTRA_DEVICE, usbDevice);
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        if(ChameleonSettings.serialIOPorts == null ||
                ChameleonSettings.serialIOPorts[ChameleonSettings.BTIO_IFACE_INDEX] == null ||
                ChameleonSettings.serialIOPorts[ChameleonSettings.USBIO_IFACE_INDEX] == null) {
            SerialUSBInterface.usbPermissionsGranted = false;
            return;
        }
        if (!ChameleonSettings.serialIOPorts[ChameleonSettings.BTIO_IFACE_INDEX].serialConfigured()) {
            if (ChameleonSettings.serialIOPorts[ChameleonSettings.USBIO_IFACE_INDEX].configureSerial() != 0) {
                ChameleonIO.DeviceStatusSettings.stopPostingStats();
                ChameleonIO.DeviceStatusSettings.startPostingStats(500);
                SerialUSBInterface.usbPermissionsGranted = true;
            }
        }

    }

}