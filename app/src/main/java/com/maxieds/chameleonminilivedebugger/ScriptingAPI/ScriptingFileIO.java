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

import android.os.Environment;

import java.io.File;

public class ScriptingFileIO {

    private static final String TAG = ScriptingFileIO.class.getSimpleName();

    private static final String STORAGE_HOME_PREFIX = "${ExtStorage}";
    public static final String DEFAULT_CMLD_DIRECTORY = STORAGE_HOME_PREFIX + "//ChameleonMiniLiveDebugger";
    public static final String DEFAULT_CMLD_SCRIPTS_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Scripts";
    public static final String DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Logging";
    public static final String DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER = DEFAULT_CMLD_DIRECTORY + "//SavedOutput";
    public static final String DEFAULT_CMLD_SCRIPT_DATA_FOLDER = DEFAULT_CMLD_DIRECTORY + "//Data";

    public static File getStorageFileFromRelative(String filePath, boolean isDir) {
        String extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath = filePath.replace(STORAGE_HOME_PREFIX, extStorageDir);
        // TODO
        return null;
    }

    public static boolean createDefaultFilePaths() {
        return true;
    }

    public static final String BINARY_FILE_TYPE = "application/octet-stream";
    public static final String TEXT_FILE_TYPE = "text/*";
    public static final String CMLD_SCRIPT_FILE_TYPE = "text/*";
    public static final String CMLD_SCRIPT_FILE_EXTS = "bat|sh|cmd|cmld";
    public static final String CMLD_SCRIPT_CONSOLE_OUTPUT_FILE_EXT = ".out";
    public static final String CMLD_SCRIPT_LOGGING_FILE_EXT = ".log";
    public static final String CMLD_SCRIPT_BINARY_DATA_FILE_EXT = ".dmp";

    /* https://developer.android.com/training/data-storage/shared/documents-files */

    public static File selectFolderFromGUIList() {
        return null;
    }

    public static File selectFileFromGUIList() {
        return null;
    }










    /*public boolean verifyScriptSourcePath(@NonNull String systemFilePath) {
        int fileExtPos = systemFilePath.lastIndexOf('.') + 1;
        if(fileExtPos > 0) {
            String fileExt = systemFilePath.substring(fileExtPos);
            List<String> fileExtList = Arrays.asList(SCRIPT_SOURCE_EXTENSIONS);
            return fileExtList.contains(fileExt);
        }
        File systemFile = new File(systemFilePath);
        if(!systemFile.exists() || systemFile.isDirectory()) {
            return false;
        }
        Uri systemFileUri = Uri.fromFile(systemFile);
        ContentResolver contentResolverSvc = LiveLoggerActivity.getLiveLoggerInstance().getContentResolver();
        if(contentResolverSvc == null) {
            return false;
        }
        String pathMimeType = contentResolverSvc.getType(systemFileUri);
        List<String> validMimeTypesList = Arrays.asList(SCRIPT_DATA_SOURCE_MIME_TYPES);
        return validMimeTypesList.contains(pathMimeType);
    }*/



}
