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

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

public class ExternalFileIO {

    private static final String TAG = ExternalFileIO.class.getSimpleName();

    public static String CHAMELEON_BASE_DIRECTORY = "//sdcard//DCIM//ChameleonMiniLiveDebugger//";
    public static String CURRENT_WORKING_DIRECTORY = CHAMELEON_BASE_DIRECTORY;

    public static String NFCTAG_BINARY_SUBDIR = "tag-bins//";
    public static String KEY_LISTS_SUBDIR = "key-lists//";
    public static String SAVED_LOGS_SUBDIR = "saved-logs//";
    public static String SCRIPTS_SUBDIR = "scripts//";
    public static String FIRMWARE_SUBDIR = "firmware//";

    public static void exportOutputFile(String fileType) {
        LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String mimeType = "message/rfc822";
        String outfilePath = "logdata-" + Utils.getTimestamp().replace(":", "") + "." + fileType;
        File downloadsFolder = new File("//sdcard//Download//");
        File outfile = new File(downloadsFolder, outfilePath);
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(),outfilePath);
        }
        else {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return;
        }
        try {
            outfile.createNewFile();
            if (fileType.equals("out")) {
                mimeType = "plain/text";
                ExportTools.writeFormattedLogFile(outfile);
            }
            else if (fileType.equals("html")) {
                mimeType = "text/html";
                ExportTools.writeHTMLLogFile(outfile);
            }
            else if (fileType.equals("bin")) {
                mimeType = "application/octet-stream";
                ExportTools.writeBinaryLogFile(outfile);
            }
        } catch(Exception ioe) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            ioe.printStackTrace();
            return;
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.getLiveLoggerInstance().defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "text/plain",
                outfile.getAbsolutePath(), outfile.length(),true);

        boolean saveFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_storage)).isChecked();
        boolean emailFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_email)).isChecked();
        boolean shareFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_share)).isChecked();
        if(emailFileChecked || shareFileChecked) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(mimeType);
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outfile));
            i.putExtra(Intent.EXTRA_SUBJECT, "Chameleon Mini Log Data Output (Log Attached)");
            i.putExtra(Intent.EXTRA_TEXT, "See subject.");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            LiveLoggerActivity.getLiveLoggerInstance().startActivity(Intent.createChooser(i, "Share the file ... "));
        }
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", "Saved log file to \"" + outfilePath + "\"."));
    }

    public static String selectFolderFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        Intent selectDirIntent = new Intent(Intent.ACTION_PICK);
        selectDirIntent.setType("vnd.android.document/directory");
        selectDirIntent.putExtra(Intent.EXTRA_MIME_TYPES, "vnd.android.cursor.dir/*");
        selectDirIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        selectDirIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Uri baseDirUri = Uri.parse(baseDirectory);
        selectDirIntent.setData(baseDirUri);
        activity.startActivityForResult(
                Intent.createChooser(selectDirIntent, "Select directory ..."),
                CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE
        );
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            try {
                String selectedDirPath = rte.getMessage().split("java.lang.RuntimeException: ")[1];
                Log.i(TAG, "Selected Text File: " + selectedDirPath);
                return selectedDirPath;
            } catch(Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public static String selectTextFileFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        Intent selectTextFileIntent = new Intent(Intent.ACTION_PICK);
        selectTextFileIntent.setType("text/*");
        selectTextFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        selectTextFileIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Uri baseDirUri = Uri.parse(baseDirectory);
        selectTextFileIntent.setData(baseDirUri);
        activity.startActivityForResult(
                Intent.createChooser(selectTextFileIntent, "Select text file ..."),
                CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE
        );
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            try {
                String selectedFilePath = rte.getMessage().split("java.lang.RuntimeException: ")[1];
                Log.i(TAG, "Selected Text File: " + selectedFilePath);
                return selectedFilePath;
            } catch(Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public static String selectFileFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        Intent selectFileIntent = new Intent(Intent.ACTION_PICK);
        selectFileIntent.setType("text/*");
        selectFileIntent.putExtra(Intent.EXTRA_MIME_TYPES, "application/octet-stream");
        selectFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        selectFileIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Uri baseDirUri = Uri.parse(baseDirectory);
        selectFileIntent.setData(baseDirUri);
        //selectFileIntent.putExtra(Intent.EXTRA_TEXT, "<Text>");
        //selectFileIntent.putExtra(Intent.EXTRA_STREAM, "<URI>");
        activity.startActivityForResult(
                Intent.createChooser(selectFileIntent, "Select file ..."),
                CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE
        );
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            try {
                String selectedFilePath = rte.getMessage().split("java.lang.RuntimeException: ")[1];
                Log.i(TAG, "Selected File: " + selectedFilePath);
                return selectedFilePath;
            } catch(Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return "";
    }

    /**
     * Constant for the file chooser dialog in the upload card data process.
     */
    public static final int FILE_SELECT_CODE = 0;
    public static final int CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE = 1 + 8080;
    public static final int CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE = 3 + 8080;

    public static void handleActivityResult(ChameleonMiniLiveDebuggerActivity activity, int requestCode, int resultCode, Intent data) {
        if(activity == null || data == null) {
            return;
        }
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    String filePath = "<FileNotFound>";
                    Cursor cursor = activity.getContentResolver().query(data.getData(), null, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        filePath = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        filePath = "//sdcard//Download//" + filePath;
                    }
                    throw new RuntimeException(filePath);
                }
                break;
            case CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String selectedDirectoryPath = "";
                    File selectedDirectory = new File(data.getData().getPath());
                    if (selectedDirectory.exists()) {
                        if (!selectedDirectory.isDirectory()) {
                            selectedDirectoryPath = selectedDirectory.getParentFile().getAbsolutePath();
                        }
                        else {
                            selectedDirectoryPath = selectedDirectory.getAbsolutePath();
                        }
                    }
                    throw new RuntimeException(selectedDirectoryPath);
                }
                break;
            case CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String selectedFilePath = "";
                    File selectedFile = new File(data.getData().getPath());
                    if (selectedFile.exists()) {
                        if (!selectedFile.isDirectory()) {
                            selectedFilePath = selectedFile.getAbsolutePath();
                        }
                    }
                    throw new RuntimeException(selectedFilePath);
                }
                break;
            default:
                break;
        }
        throw new RuntimeException("");
    }

}
