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
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.RadioButton;

import com.maxieds.androidfilepickerlightlibrary.FileChooserBuilder;

import java.io.File;
import java.util.List;

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
        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        llActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String mimeType = "message/rfc822";
        String outfilePath = "logdata-" + Utils.getTimestamp().replace(":", "") + "." + fileType;
        //File downloadsFolder = new File("//sdcard//Download//");
        File downloadsFolder = new File(llActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "//Download//");
        File outfile = new File(downloadsFolder, outfilePath);
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(),outfilePath);
        }
        else {
            GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            llActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
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
            GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            llActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            AndroidLogger.printStackTrace(ioe);
            return;
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.getLiveLoggerInstance().defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "text/plain",
                                             outfile.getAbsolutePath(), outfile.length(),true);

        boolean saveFileChecked = false, emailFileChecked = false, shareFileChecked = false;
        try {
            saveFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_storage)).isChecked();
            emailFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_email)).isChecked();
            shareFileChecked = ((RadioButton) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.radio_save_share)).isChecked();
        } catch(NullPointerException npe) {
            AndroidLogger.printStackTrace(npe);
        }
        if(emailFileChecked || shareFileChecked) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType(mimeType);
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outfile));
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Chameleon Mini Log Data Output (Log Attached)");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "See the subject.");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            llActivity.startActivity(Intent.createChooser(sendIntent, "Share the file ... "));
        }
        GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", "Saved log file to \"" + outfilePath + "\"."));
    }

    /**
     * Constant for the file chooser dialog in the upload card data process.
     */
    public static final int FILE_SELECT_CODE = 0;

    public static void handleActivityResult(ChameleonMiniLiveDebuggerActivity activity, int requestCode, int resultCode, Intent data) {
        if(activity == null || data == null) {
            throw new RuntimeException("");
        }
        boolean handleChooserResult = false;
        int chooserRequestCodeAction = requestCode;
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    String filePath = "<FileNotFound>";
                    Cursor cursor = activity.getContentResolver().query(data.getData(), null, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        filePath = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        //filePath = "//sdcard//Download//" + filePath;
                        filePath = LiveLoggerActivity.getLiveLoggerInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "//Download//" + filePath;
                    }
                    throw new RuntimeException(filePath);
                }
                break;
            case AndroidFileChooser.ACTION_SELECT_DIRECTORY_ONLY:
                handleChooserResult = true;
                chooserRequestCodeAction = FileChooserBuilder.ACTIVITY_CODE_SELECT_DIRECTORY_ONLY;
                break;
            case AndroidFileChooser.ACTION_SELECT_FILE_ONLY:
                handleChooserResult = true;
                chooserRequestCodeAction = FileChooserBuilder.ACTIVITY_CODE_SELECT_FILE_ONLY;
                break;
            default:
                break;
        }
        if (handleChooserResult && resultCode == RESULT_OK) {
            String selectedChooserPath = "";
            try {
                List<String> selectedFilePathsList = FileChooserBuilder.handleActivityResult(activity, chooserRequestCodeAction, resultCode, data);
                selectedChooserPath = String.format(BuildConfig.DEFAULT_LOCALE, AndroidFileChooser.getFileNotifySelectExceptionFormat(), selectedFilePathsList.get(0));
            } catch(Exception ex) {
                AndroidLogger.printStackTrace(ex);
            }
            throw new RuntimeException(selectedChooserPath);
        }
        throw new RuntimeException("");
    }

}
