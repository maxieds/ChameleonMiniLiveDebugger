package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class SerialUSBInterface implements ChameleonSerialIOInterface {

    private static final String TAG = SerialUSBInterface.class.getSimpleName();

    public static final int USB_DATA_BITS = 16;

    private Context notifyContext;
    private UsbSerialDevice serialPort;
    private UsbDevice activeDevice;
    private UsbSerialInterface.UsbReadCallback serialReaderCallback;
    private int baudRate;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private final Semaphore serialPortLock = new Semaphore(1, true);

    public SerialUSBInterface(Context context) {
        notifyContext = context;
        serialPort = null;
        baudRate = Settings.serialBaudRate;
        serialConfigured = false;
        receiversRegistered = false;
    }

    public void setListenerContext(Context context) {
        notifyContext = context;
    }

    public boolean notifySerialDataReceived(byte[] serialData) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyLogDataReceived(byte[] serialData) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceFound() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceConnectionTerminated() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyStatus(String msgType, String statusMsg) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
        notifyIntent.putExtra("STATUS-TYPE", msgType);
        notifyIntent.putExtra("STATUS-MSG", statusMsg);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean isWiredUSB() { return true; }

    public boolean isBluetooth() { return false; }

    public int setSerialBaudRate(int brate) {
        baudRate = brate;
        Settings.serialBaudRate = baudRate;
        if(serialPort != null) {
            serialPort.setBaudRate(baudRate);
            return baudRate;
        }
        return 0;
    }
    public int setSerialBaudRateHigh() {
        return setSerialBaudRate(ChameleonSerialIOInterface.HIGH_SPEED_BAUD_RATE);
    }
    public int setSerialBuadRateLimited() {
        return setSerialBaudRate(ChameleonSerialIOInterface.LIMITED_SPEED_BAUD_RATE);
    }

    private Handler scanDeviceHandler = new Handler();
    private Runnable scanDeviceRunnable = new Runnable() {
        public void run() {
            if (Settings.getActiveSerialIOPort() != null || configureSerial() != 0) {
                scanDeviceHandler.removeCallbacksAndMessages(this);
            } else {
                scanDeviceHandler.removeCallbacksAndMessages(this);
                scanDeviceHandler.postDelayed(this, SCAN_POST_TIME_DELAY);
            }
        }
    };
    private static final int SCAN_POST_TIME_DELAY = 1250;

    public boolean startScanningDevices() {
        scanDeviceHandler.post(scanDeviceRunnable);
        return true;
    }

    public boolean stopScanningDevices() {
        scanDeviceHandler.removeCallbacksAndMessages(scanDeviceRunnable);
        return true;
    }

    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "";
        }
        String devInfo = String.format(Locale.ENGLISH, "Manufacturer: %s\nProduct Name: %s\nVersion: %s\nDevice Serial: %s\nUSB Dev: %s",
                                       activeDevice.getManufacturerName(), activeDevice.getProductName(),
                                       activeDevice.getVersion(), activeDevice.getSerialNumber(),
                                       activeDevice.getDeviceName());
        return devInfo;
    }

    public int configureSerial() {
        if(serialConfigured()) {
            return 1;
        }
        UsbManager usbManager = (UsbManager) notifyContext.getSystemService(Context.USB_SERVICE);
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
        //    notifyStatus("USB STATUS: ", "Connection to device unavailable.");
            serialPort = null;
            return 0;
        }
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialPort != null && serialPort.open()) {
            serialPort.setBaudRate(Settings.serialBaudRate);
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
            return 0;
        }
        activeDevice = device;
        Settings.chameleonDeviceSerialNumber = activeDevice.getSerialNumber();
        ChameleonIO.PAUSED = false;
        serialConfigured = true;
        receiversRegistered = true;
        Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.USBIO_IFACE_INDEX;
        LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUSB, R.drawable.usbconnected16);
        notifyStatus("USB STATUS: ", "Successfully configured the device in passive logging mode...\n" + getActiveDeviceInfo());
        return 1;
    }

    public int shutdownSerial() {
        if(serialPort != null)
            serialPort.close();
        ChameleonIO.PAUSED = true;
        ExportTools.EOT = true;
        ExportTools.transmissionErrorOccurred = true;
        ChameleonIO.DOWNLOAD = false;
        ChameleonIO.UPLOAD = false;
        ChameleonIO.WAITING_FOR_XMODEM = false;
        ChameleonIO.WAITING_FOR_RESPONSE = false;
        ChameleonIO.EXPECTING_BINARY_DATA = false;
        ChameleonIO.LASTCMD = "";
        serialPort = null;
        activeDevice = null;
        serialConfigured = false;
        receiversRegistered = false;
        //notifyDeviceConnectionTerminated();
        return 1;
    }

    private UsbSerialInterface.UsbReadCallback createSerialReaderCallback() {
        return new UsbSerialInterface.UsbReadCallback() {
            @Override
            public void onReceivedData(byte[] liveLogData) {
                Log.d(TAG, "USBReaderCallback Received Data: " + Utils.bytes2Hex(liveLogData));
                Log.d(TAG, "    => " + Utils.bytes2Ascii(liveLogData));
                if(ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData)) {
                    notifyLogDataReceived(liveLogData);
                    return;
                }
                else if(ChameleonIO.PAUSED) {
                    return;
                }
                else if(ChameleonIO.DOWNLOAD) {
                    ExportTools.performXModemSerialDownload(liveLogData);
                    return;
                }
                else if(ChameleonIO.UPLOAD) {
                    ExportTools.performXModemSerialUpload(liveLogData);
                    return;
                }
                else if(ChameleonIO.WAITING_FOR_XMODEM) {
                    String strLogData = new String(liveLogData);
                    if(strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                        ChameleonIO.WAITING_FOR_XMODEM = false;
                        return;
                    }
                }
                else if(ChameleonIO.WAITING_FOR_RESPONSE && ChameleonIO.isCommandResponse(liveLogData)) {
                    String[] strLogData = (new String(liveLogData)).split("[\n\r\t]+");
                    ChameleonIO.DEVICE_RESPONSE_CODE = strLogData[0];
                    if(strLogData.length >= 2)
                        ChameleonIO.DEVICE_RESPONSE = Arrays.copyOfRange(strLogData, 1, strLogData.length);
                    else
                        ChameleonIO.DEVICE_RESPONSE[0] = strLogData[0];
                    if(ChameleonIO.EXPECTING_BINARY_DATA) {
                        int binaryBufSize = liveLogData.length - ChameleonIO.DEVICE_RESPONSE_CODE.length() - 2;
                        ChameleonIO.DEVICE_RESPONSE_BINARY = new byte[binaryBufSize];
                        System.arraycopy(liveLogData, liveLogData.length - binaryBufSize, ChameleonIO.DEVICE_RESPONSE_BINARY, 0, binaryBufSize);
                        ChameleonIO.EXPECTING_BINARY_DATA = false;
                    }
                    ChameleonIO.WAITING_FOR_RESPONSE = false;
                    return;
                }
                notifySerialDataReceived(liveLogData);
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
            inte.printStackTrace();
            serialPortLock.release();
            return false;
        }
    }

    public boolean acquireSerialPortNoInterrupt() {
        try {
            serialPortLock.acquireUninterruptibly();
            return true;
        } catch(Exception inte) {
            inte.printStackTrace();
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
            inte.printStackTrace();
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
            return 0;
        }
        else if(!serialConfigured()) {
            return 0;
        }
        serialPort.write(dataWriteBuffer);
        return 1;
    }

}