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
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.maxieds.chameleonminilivedebugger.AndroidFileChooser;
import com.maxieds.chameleonminilivedebugger.AndroidLog;
import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
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
    public static final String CMLD_SCRIPT_DEBUGGING_FILE_EXT = ".debug";
    public static final String CMLD_SCRIPT_BINARY_DATA_FILE_EXT = ".dmp";

    private static final String PATH_SEPARATOR = "/";
    private static final String STORAGE_HOME_PREFIX = "${EXT}";
    private static final String STORAGE_HOME_PREFIX_SUBST = "\\$\\{EXT\\}" + PATH_SEPARATOR;

    public static final String  DEFAULT_CMLD_DIRECTORY = STORAGE_HOME_PREFIX + "/CMLD";
    public static final String  DEFAULT_CMLD_SCRIPTS_FOLDER = DEFAULT_CMLD_DIRECTORY + "/Scripts";
    public static final String  DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER = DEFAULT_CMLD_DIRECTORY + "/Logs";
    public static final String  DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER = DEFAULT_CMLD_DIRECTORY + "/Output";
    public static final String  DEFAULT_CMLD_SCRIPT_DATA_FOLDER = DEFAULT_CMLD_DIRECTORY + "/Data";

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

    public static File getStoragePathFromRelative(String filePath, boolean createFile, boolean isDir) {
        String extStorageDir = (LiveLoggerActivity.getLiveLoggerInstance().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + PATH_SEPARATOR).replace("//", "/");
        filePath = filePath.replaceAll(STORAGE_HOME_PREFIX_SUBST, extStorageDir);
        File storageFile = new File(filePath);
        boolean setPermissions = true;
        if(createFile && isDir && !storageFile.exists()) {
            try {
                Files.createDirectories(storageFile.toPath());
            } catch(IOException ioe) {
                AndroidLog.printStackTrace(ioe);
                return null;
            }
        }
        else if(createFile && !isDir && !storageFile.exists()) {
            try {
                Files.createDirectories(storageFile.getParentFile().toPath());
                storageFile.createNewFile();
            } catch(IOException ioe) {
                AndroidLog.printStackTrace(ioe);
                return null;
            }
        }
        else if(storageFile.exists()) {
            setPermissions = false;
        }
        if(setPermissions) {
            try {
                if(isDir || storageFile.isDirectory()) {
                    Files.setPosixFilePermissions(storageFile.toPath(), PosixFilePermissions.fromString("rwxr-x---"));
                }
                else {
                    Files.setPosixFilePermissions(storageFile.toPath(), PosixFilePermissions.fromString("rw-r-----"));
                }
            } catch(IOException ioe) {
                AndroidLog.printStackTrace(ioe);
                return null;
            }
        }
        return storageFile;
    }

    public static final int DISPLAY_TEXT_MAX_LENGTH = 35;
    public static final int SHORTENED_PATH_INDEX = 0;
    public static final int COMPLETE_PATH_INDEX = 1;

    public static String[] shortenStoragePath(String fullPath, int maxLength) {
        if(maxLength <= 0) {
            return null;
        }
        else if(fullPath.length() <= DISPLAY_TEXT_MAX_LENGTH) {
            return new String[] { fullPath, fullPath };
        }
        String extStorageDir = AndroidFileChooser.getInitialFileChooserBaseFolder().replace("//", "/");
        String shortenedPath = fullPath.replace("//", "/").replace(extStorageDir, STORAGE_HOME_PREFIX);
        if(fullPath.contains(STORAGE_HOME_PREFIX)) {
            int fullPathAfterIdx = fullPath.indexOf(STORAGE_HOME_PREFIX) + STORAGE_HOME_PREFIX.length() + 1;
            String prefixPath = shortenedPath.substring(0, fullPathAfterIdx);
            int suffixLength = Math.min(Math.max(0, maxLength - 5), shortenedPath.length() + 1 - fullPathAfterIdx);
            String suffixPath = "";
            if(suffixLength < shortenedPath.length() - fullPathAfterIdx) {
                suffixPath = "<...>" + shortenedPath.substring(shortenedPath.length() + 1 - suffixLength);
            }
            else {
                suffixPath = shortenedPath.substring(fullPathAfterIdx + 1);
            }
            shortenedPath = prefixPath + suffixPath;
        }
        else {
            String prefixPath = "";
            int suffixLength = Math.min(Math.max(0, maxLength - 5), shortenedPath.length());
            String suffixPath = "";
            if(suffixLength < shortenedPath.length()) {
                suffixPath = "<...>" + shortenedPath.substring(shortenedPath.length() - suffixLength - 1);
            }
            else {
                suffixPath = shortenedPath;
            }
            shortenedPath = prefixPath + suffixPath;
        }
        return new String[] {
                shortenedPath,
                fullPath
        };
    }

    public static String expandStoragePath(String initFilePath) {
        String extStorageDir = (AndroidFileChooser.getInitialFileChooserBaseFolder() + PATH_SEPARATOR).replace("//", "/");
        return initFilePath.replaceAll(STORAGE_HOME_PREFIX_SUBST, extStorageDir);
    }

    public static boolean createDefaultFilePaths() {
        File defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_DIRECTORY, true, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPTS_FOLDER, true, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER, true, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER, true, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        defaultCMLDPath = getStoragePathFromRelative(DEFAULT_CMLD_SCRIPT_DATA_FOLDER, true, true);
        if(defaultCMLDPath == null) {
            return false;
        }
        return true;
    }

    public static String selectDirectoryFromGUIList(@NonNull String baseDirectory) {
        return AndroidFileChooser.selectFolderFromGUIList(baseDirectory, true);
    }

    public static String selectFileFromGUIList(@NonNull String baseDirectory) {
        return AndroidFileChooser.selectFileFromGUIList(baseDirectory, true);
    }

    public static String getScriptOutputFilePath(String scriptFilePath) {
        File scriptFile = ScriptingFileIO.getStoragePathFromRelative(scriptFilePath, false, false);
        String scriptFileBaseName = scriptFile.getName().replace("\\.[a-zA-Z0-9]+", "");
        String outputFileBaseName = ScriptingConfig.OUTPUT_FILE_BASENAME.equals("") ? scriptFileBaseName : ScriptingConfig.OUTPUT_FILE_BASENAME;
        if(ScriptingConfig.DATESTAMP_OUTPUT_FILES && !ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE) {
            outputFileBaseName += "-" + ScriptingConfig.DATESTAMP_FORMAT;
            Time currentTime = new Time();
            currentTime.setToNow();
            outputFileBaseName = currentTime.format(outputFileBaseName);
        }
        outputFileBaseName +=  CMLD_SCRIPT_CONSOLE_OUTPUT_FILE_EXT;
        outputFileBaseName = expandStoragePath(ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER + "//" + outputFileBaseName);
        ScriptingFileIO.getStoragePathFromRelative(outputFileBaseName, true, false);
        return outputFileBaseName;
    }

    public static String getScriptLoggingFilePath(String scriptFilePath) {
        File scriptFile = ScriptingFileIO.getStoragePathFromRelative(scriptFilePath, false, false);
        String scriptFileBaseName = scriptFile.getName().replace("\\.[a-zA-Z0-9]+", "");
        String loggingFileBaseName = ScriptingConfig.OUTPUT_LOGFILE_BASENAME.equals("") ? scriptFileBaseName : ScriptingConfig.OUTPUT_LOGFILE_BASENAME;
        if(ScriptingConfig.DATESTAMP_OUTPUT_FILES && !ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE) {
            loggingFileBaseName += "-" + ScriptingConfig.DATESTAMP_FORMAT;
            Time currentTime = new Time();
            currentTime.setToNow();
            loggingFileBaseName = currentTime.format(loggingFileBaseName);
        }
        loggingFileBaseName +=  CMLD_SCRIPT_LOGGING_FILE_EXT;
        loggingFileBaseName = expandStoragePath(ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER + "//" + loggingFileBaseName);
        ScriptingFileIO.getStoragePathFromRelative(loggingFileBaseName, true, false);
        return loggingFileBaseName;
    }

    public static String getScriptDebuggingFilePath(String scriptFilePath) {
        if(!ScriptingConfig.VERBOSE_ERROR_LOGGING) {
            return ScriptingTypes.NULL;
        }
        File scriptFile = ScriptingFileIO.getStoragePathFromRelative(scriptFilePath, false, false);
        String scriptFileBaseName = scriptFile.getName().replace("\\.[a-zA-Z0-9]+", "");
        String debuggingFileBaseName = scriptFileBaseName + "-" + ScriptingConfig.DATESTAMP_FORMAT;
        Time currentTime = new Time();
        currentTime.setToNow();
        debuggingFileBaseName = currentTime.format(debuggingFileBaseName);
        debuggingFileBaseName +=  CMLD_SCRIPT_DEBUGGING_FILE_EXT;
        debuggingFileBaseName = expandStoragePath(ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER + "//" + debuggingFileBaseName);
        ScriptingFileIO.getStoragePathFromRelative(debuggingFileBaseName, true, false);
        return debuggingFileBaseName;
    }

}
