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

package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ChameleonScriptingException;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ExceptionType;

import android.os.Handler;
import android.util.Log;

import com.maxieds.chameleonminilivedebugger.ChameleonIO;
import com.maxieds.chameleonminilivedebugger.ChameleonLogUtils;
import com.maxieds.chameleonminilivedebugger.ChameleonSerialIOInterface;
import com.maxieds.chameleonminilivedebugger.ChameleonSettings;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariableArrayMap;
import com.maxieds.chameleonminilivedebugger.Utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChameleonIOHandler implements ChameleonSerialIOInterface.SerialDataReceiverInterface {

    private static final String TAG = ChameleonIOHandler.class.getSimpleName();

    private static boolean PAUSED = false;
    private static boolean DOWNLOAD = false;
    private static boolean UPLOAD = false;
    private static boolean WAITING_FOR_XMODEM = false;
    private static boolean WAITING_FOR_RESPONSE = false;

    private static Lock incomingIOBufferAccessLock = new ReentrantLock();
    private static Queue<Byte[]> incomingSerialIOBufferQueue = new LinkedBlockingQueue<>();

    private static Thread processIncomingSerialIODataThread = new Thread() {
        @Override
        public void run() {
            incomingIOBufferAccessLock.lock();
            if(incomingSerialIOBufferQueue.size() > 0) {
                final byte[] nextIncomingBytes = ArrayUtils.toPrimitive(incomingSerialIOBufferQueue.remove());
                Handler delayRunHandler = new Handler();
                Runnable delayRunner = new Runnable() {
                    @Override
                    public void run() {
                        registerNewSerialIODataBuffer(nextIncomingBytes);
                    }
                };
                delayRunHandler.postDelayed(delayRunner, 150);
            }
            incomingIOBufferAccessLock.unlock();
        }
    };

    public static void registerNewSerialIODataBuffer(byte[] incomingDataBytes) {
        incomingIOBufferAccessLock.lock(); // TODO: Watch for hangs ...
        if(processIncomingSerialIODataThread.isAlive()) {
            incomingSerialIOBufferQueue.add(ArrayUtils.toObject(incomingDataBytes));
        }
        else {
            handleChameleonSerialResults(incomingDataBytes);
            processIncomingSerialIODataThread.start();
        }
        incomingIOBufferAccessLock.unlock();
    }

    private static Lock statusConfigLock = new ReentrantLock();

    public void onReceivedData(byte[] dataBytes) {
        registerNewSerialIODataBuffer(dataBytes);
    }

    public static void handleChameleonSerialResults(byte[] dataBytes) {
        int loggingRespSize = ChameleonLogUtils.ResponseIsLiveLoggingBytes(dataBytes);
        if(loggingRespSize > 0) {
            if(!ScriptingConfig.IGNORE_LIVE_LOGGING) {
                Log.i(TAG, "Received LIVE logging data [" + ChameleonLogUtils.LogCode.lookupByLogCode(dataBytes[0]).toString() + "]");
            }
        }
        else if(PAUSED) {}
        else if(DOWNLOAD) {}
        else if(UPLOAD) {}
        else if (WAITING_FOR_XMODEM) {
            String strLogData = new String(dataBytes);
            if (strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                ChameleonIO.WAITING_FOR_XMODEM = false;
                return;
            }
        }
        else if(ChameleonIO.isCommandResponse(dataBytes) && WAITING_FOR_RESPONSE) {
            statusConfigLock.lock();
            WAITING_FOR_RESPONSE = false;
            final String chameleonCmdResponse = String.valueOf(dataBytes);
            Handler execCmdResponseRtexHandler = new Handler();
            Runnable execCmdResponseRtexRunner = new Runnable() {
                @Override
                public void run() {
                    throw new RuntimeException(chameleonCmdResponse);
                }
            };
            execCmdResponseRtexHandler.postDelayed(execCmdResponseRtexRunner, 200);
            statusConfigLock.unlock();
        }
        else {
            Log.i(TAG, "Received unexpected Serial I/O @ " + Utils.bytes2Hex(dataBytes));
        }
    }

    private static int CHAMELEON_TIMEOUT = ChameleonIO.TIMEOUT;
    private static final int sleepDeltaMs = 50;

    public static ScriptVariable executeChameleonCommandForResult(String cmdText) {
        return executeChameleonCommandForResult(cmdText, CHAMELEON_TIMEOUT);
    }

    public static ScriptVariable executeChameleonCommandForResult(String cmdText, int timeout) {
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null) {
            throw new ChameleonScriptingException(ExceptionType.ChameleonDisconnectedException, "Serial port is null -- Is the Chameleon attached?");
        }
        if(!serialIOPort.tryAcquireSerialPort(ChameleonIO.LOCK_TIMEOUT)) {
            return parseChameleonCommandResponse(cmdText, "", true);
        }
        String sendCmd = cmdText + (ChameleonIO.REVE_BOARD ? "\r\n" : "\n\r");
        serialIOPort.sendDataBuffer(sendCmd.getBytes());
        String cmdResp = "";
        boolean isTimeout = true;
        for(int i = 0; i < timeout / sleepDeltaMs; i++) {
            if(!WAITING_FOR_RESPONSE) {
                isTimeout = false;
                break;
            }
            try {
                Thread.sleep(sleepDeltaMs);
            } catch(InterruptedException ie) {
                WAITING_FOR_RESPONSE = false;
                isTimeout = false;
                cmdResp = ie.getMessage().replace("java.lang.RuntimeException: ", "");
                break;
            }
        }
        return parseChameleonCommandResponse(cmdText, cmdResp, isTimeout);
    }

    // Named fields in the hashed array variable returned include:
    // ->cmdName
    // ->respCode
    // ->respText
    // ->data (possibly empty)
    // ->isError
    // ->isTimeout
    public static ScriptVariable parseChameleonCommandResponse(String cmdData, String response, boolean isTimeout) {

        ScriptVariable cmdRespVar = new ScriptVariableArrayMap();
        Pattern cmdNamePattern = Pattern.compile("\\([a-zA-Z0-9]+\\)[=\\? ]");
        Matcher cmdNameMatcher = cmdNamePattern.matcher(cmdData);
        String cmdName = cmdData;
        if(cmdNameMatcher.find()) {
            cmdName = cmdNameMatcher.group();
        }
        cmdRespVar.setValueAt("cmdName", ScriptVariable.newInstance().set(cmdName));

        try {
            String[] splitRespData = response.split("[\n\r\t][\n\r\t]+");
            int respCode = -1;
            if(splitRespData.length > 0) {
                String[] cmdRespText = splitRespData[0].split(":");
                respCode = Integer.parseInt(cmdRespText[0], 10);
                cmdRespVar.setValueAt("respCode", ScriptVariable.newInstance().set(respCode));
                cmdRespVar.setValueAt("respText", ScriptVariable.newInstance().set(cmdRespText[1]));
            }
            else {
                cmdRespVar.setValueAt("respCode", ScriptVariable.newInstance().set(""));
                cmdRespVar.setValueAt("respText", ScriptVariable.newInstance().set(""));
            }
            if (splitRespData.length > 1) {
                String[] dataArr = new String[splitRespData.length - 1];
                System.arraycopy(splitRespData, 1, dataArr, 0, splitRespData.length - 1);
                String dataValue = StringUtils.join(dataArr, "\n");
                cmdRespVar.setValueAt("data", ScriptVariable.newInstance().set(dataValue));
            }
            ChameleonIO.SerialRespCode chameleonRespCode = ChameleonIO.SerialRespCode.lookupByResponseCode(respCode);
            if (chameleonRespCode == ChameleonIO.SerialRespCode.OK ||
                    chameleonRespCode == ChameleonIO.SerialRespCode.OK_WITH_TEXT ||
                    chameleonRespCode == ChameleonIO.SerialRespCode.TRUE) {
                cmdRespVar.setValueAt("isError", ScriptVariable.newInstance().set(false));
            } else {
                cmdRespVar.setValueAt("isError", ScriptVariable.newInstance().set(true));
            }
            if (isTimeout || chameleonRespCode == ChameleonIO.SerialRespCode.TIMEOUT) {
                cmdRespVar.setValueAt("isTimeout", ScriptVariable.newInstance().set(true));
            } else {
                cmdRespVar.setValueAt("isTimeout", ScriptVariable.newInstance().set(false));
            }
            ScriptingGUIConsole.appendConsoleOutputRecordChameleonCommandResponse(cmdRespVar, -1);
            return cmdRespVar;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
