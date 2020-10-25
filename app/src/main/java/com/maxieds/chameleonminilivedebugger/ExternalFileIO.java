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
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.RadioButton;

import java.io.File;

import me.rosuh.filepicker.config.FilePickerManager;

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
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType(mimeType);
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outfile));
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Chameleon Mini Log Data Output (Log Attached)");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "See the subject.");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            LiveLoggerActivity.getLiveLoggerInstance().startActivity(Intent.createChooser(sendIntent, "Share the file ... "));
        }
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", "Saved log file to \"" + outfilePath + "\"."));
    }

    /**
     * Constant for the file chooser dialog in the upload card data process.
     */
    public static final int FILE_SELECT_CODE = 0;
    public static final int CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE = 1 + 8080;
    public static final int CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE = 3 + 8080;

    public static void handleActivityResult(ChameleonMiniLiveDebuggerActivity activity, int requestCode, int resultCode, Intent data) {
        if(activity == null || data == null) {
            throw new RuntimeException("");
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
                    String selectedDirectoryPath = FilePickerManager.INSTANCE.obtainData().size() > 0 ? FilePickerManager.INSTANCE.obtainData().get(0) : "";
                    Log.i(TAG, "Dir PATH: " + selectedDirectoryPath);
                    throw new RuntimeException(selectedDirectoryPath);
                }
                break;
            case CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String selectedFilePath = FilePickerManager.INSTANCE.obtainData().size() > 0 ? FilePickerManager.INSTANCE.obtainData().get(0) : "";
                    Log.i(TAG, "File PATH: " + selectedFilePath);
                    throw new RuntimeException(selectedFilePath);
                }
                break;
            default:
                break;
        }
        throw new RuntimeException("");
    }

}
