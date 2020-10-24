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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.maxieds.chameleonminilivedebugger.ChameleonMiniLiveDebuggerActivity;
import com.maxieds.chameleonminilivedebugger.ExternalFileIO;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;

public class ScriptingFileIO {

    private static final String TAG = ScriptingFileIO.class.getSimpleName();

    public static final String BINARY_FILE_MIME_TYPE = "application/octet-stream";
    public static final String TEXT_FILE_MIME_TYPE = "text/plain";
    public static final String CMLD_SCRIPT_FILE_MIME_TYPE = "text/*";
    public static final String CMLD_SCRIPT_CONSOLE_OUTPUT_FILE_EXT = ".out";
    public static final String CMLD_SCRIPT_LOGGING_FILE_EXT = ".log";
    public static final String CMLD_SCRIPT_BINARY_DATA_FILE_EXT = ".dmp";

    private static final String STORAGE_HOME_PREFIX = "${ExtStorage}";
    public static final String  DEFAULT_CMLD_DIRECTORY = STORAGE_HOME_PREFIX + "//ChameleonMiniLiveDebugger";
    public static final String  DEFAULT_CMLD_SCRIPTS_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Scripts";
    public static final String  DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Logging";
    public static final String  DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER = DEFAULT_CMLD_DIRECTORY + "//SavedOutput";
    public static final String  DEFAULT_CMLD_SCRIPT_DATA_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Data";

    private static boolean checkExternalStoragePermissions(boolean displayToastOnFail) {
        if(ContextCompat.checkSelfPermission(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(displayToastOnFail) {
                Utils.displayToastMessageLong("No write external storage permissions!");
            }
            return false;
        }
        else if(ContextCompat.checkSelfPermission(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(displayToastOnFail) {
                Utils.displayToastMessageLong("No read external storage permissions!");
            }
            return false;
        }
        return true;
    }

    public static File getStoragePathFromRelative(String filePath, boolean createFile) {
        if(!checkExternalStoragePermissions(true)) {
            return null;
        }
        String extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath = filePath.replace(STORAGE_HOME_PREFIX, extStorageDir);
        File storageFile = new File(filePath);
        boolean setPermissions = true;
        if(createFile && !storageFile.exists() && storageFile.isDirectory()) {
            storageFile.mkdirs();
        }
        else if(createFile && !storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
        }
        else if(storageFile.exists()) {
            setPermissions = false;
        }
        if(setPermissions) {
            try {
                if(storageFile.isDirectory()) {
                    Files.setPosixFilePermissions(storageFile.toPath(), PosixFilePermissions.fromString("rwxr-x---"));
                }
                else {
                    Files.setPosixFilePermissions(storageFile.toPath(), PosixFilePermissions.fromString("rw-r-----"));
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
        }
        return storageFile;
    }

    public static boolean createDefaultFilePaths() {
        if(!checkExternalStoragePermissions(true)) {
            return false;
        }
        File defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPTS_FOLDER, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_DATA_FOLDER, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        return true;
    }

    public static String selectDirectoryFromGUIList(@NonNull String baseDirectory) {
        return ExternalFileIO.selectFolderFromGUIList(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT, baseDirectory);
    }

    public static String selectTextFileFromGUIList(@NonNull String baseDirectory) {
        return ExternalFileIO.selectTextFileFromGUIList(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT, baseDirectory);
    }

    public static String selectFileFromGUIList(@NonNull String baseDirectory) {
        return ExternalFileIO.selectFileFromGUIList(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT, baseDirectory);
    }

}
