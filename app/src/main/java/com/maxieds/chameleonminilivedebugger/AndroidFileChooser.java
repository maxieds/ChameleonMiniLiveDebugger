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

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.maxieds.androidfilepickerlightlibrary.CustomThemeBuilder;
import com.maxieds.androidfilepickerlightlibrary.FileChooserBuilder;

import java.util.Locale;

public class AndroidFileChooser {

    private static final String TAG = AndroidFileChooser.class.getSimpleName();

    private static final int CMLD_PICKER_ACTION_SWIZZLE_CODE = (42691 << 2) + 3;
    public static final int ACTION_SELECT_DIRECTORY_ONLY = 1 + CMLD_PICKER_ACTION_SWIZZLE_CODE;
    public static final int ACTION_SELECT_FILE_ONLY = 2 + CMLD_PICKER_ACTION_SWIZZLE_CODE;

    private static final long PICKER_SLEEP_DELAY_MILLIS = 75;
    private static final long PICKER_MAX_ALIVE_RUNTIME = 60000L; /* After 60 seconds, kill the picker if the user has not made a selection */

    public static final FileChooserBuilder.BaseFolderPathType CONFIG_DEFAULT_STORAGE_TYPE = FileChooserBuilder.BaseFolderPathType.BASE_PATH_DEFAULT;

    public static String getFileNotifySelectExceptionFormat() {
        return "PATHSELECTION:==%s==";
    }

    public static CustomThemeBuilder getFileChooserCustomStyle() {
        CustomThemeBuilder customThemeBuilder = new CustomThemeBuilder(LiveLoggerActivity.getLiveLoggerInstance())
                .setPickerTitleText(R.string.filePickerTitleTextSelectFile)
                .setNavBarPrefixText(R.string.filePickerNavbarPrefixText)
                .setDoneActionButtonText(R.string.filePickerDoneActionText)
                .setCancelActionButtonText(R.string.filePickerCancelActionText)
                .setGlobalBackButtonIcon(R.drawable.filepicker_nav_back_button_icon32)
                .setDoneActionButtonIcon(R.drawable.filepicker_done_button_check_icon24)
                .setCancelActionButtonIcon(R.drawable.filepicker_cancel_button_x_icon24)
                .generateThemeColors(R.color.AppThemeGreen_colorPrimary)
                .setActivityToolbarIcon(R.drawable.filepicker_toolbar_logo_icon)
                .useToolbarGradients(true)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_sdcard_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_ROOT_STORAGE)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_images_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_PICTURES)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_camera_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_CAMERA)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_screenshots_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_SCREENSHOTS)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_downloads_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_DOWNLOADS)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_user_home_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_USER_HOME)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_media_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_MEDIA_VIDEO)
                .setNavigationByPathButtonIcon(R.drawable.filepicker_named_folder_recents_icon32, FileChooserBuilder.DefaultNavFoldersType.FOLDER_RECENT_DOCUMENTS)
                .setDefaultFileIcon(R.drawable.filepicker_generic_file_icon32)
                .setDefaultHiddenFileIcon(R.drawable.filepicker_hidden_file_icon32)
                .setDefaultFolderIcon(R.drawable.filepicker_folder_icon32);
        return customThemeBuilder;
    }

    public static String runFileChooserForResult(FileChooserBuilder.SelectionModeType selectionMode, @NonNull String baseDirectory, boolean basePathIsRelative) {

        int pickerTitleTextResId = 0;
        int activityActionCode = 0;
        if(selectionMode.ordinal() == FileChooserBuilder.SelectionModeType.SELECT_DIRECTORY_ONLY.ordinal()) {
            pickerTitleTextResId = R.string.filePickerTitleTextSelectFolder;
            activityActionCode = ACTION_SELECT_DIRECTORY_ONLY;
        }
        else {
            pickerTitleTextResId = R.string.filePickerTitleTextSelectFolder;
            activityActionCode = ACTION_SELECT_FILE_ONLY;
        }

        CustomThemeBuilder customChooserTheme = getFileChooserCustomStyle();
        customChooserTheme.setPickerTitleText(pickerTitleTextResId);
        FileChooserBuilder fcBuilder = new FileChooserBuilder(LiveLoggerActivity.getLiveLoggerInstance())
                .setActionCode(activityActionCode)
                .setSelectMultiple(1)
                .setSelectionMode(selectionMode)
                .showHidden(true)
                .setNavigationLongForm(true)
                .setCustomThemeStylizerConfig(customChooserTheme);
        if(basePathIsRelative) {
            fcBuilder.setInitialPath(CONFIG_DEFAULT_STORAGE_TYPE, baseDirectory);
        }
        else {
            fcBuilder.setInitialPathAbsolute(baseDirectory);
        }
        fcBuilder.launchFilePicker();

        final Thread callingThread = Thread.currentThread();
        final Handler killFileChooserTimeoutHandler = new Handler();
        final Runnable killFileChooserTimeoutSaveRunner = new Runnable() {
            private Thread callbackToThread = callingThread;
            @Override
            public void run() {
                callbackToThread.interrupt();
            }
        };
        killFileChooserTimeoutHandler.postDelayed(killFileChooserTimeoutSaveRunner, PICKER_MAX_ALIVE_RUNTIME);
        while(true) {
            try {
                Thread.sleep(PICKER_SLEEP_DELAY_MILLIS);
            } catch(InterruptedException ie) {
                killFileChooserTimeoutHandler.removeCallbacks(killFileChooserTimeoutSaveRunner);
                String excptMsg = ie.getMessage();
                String returnedPathRegex = String.format(Locale.getDefault(), getFileNotifySelectExceptionFormat(), "([a-zA-Z0-9_.-]+)");
                if(!excptMsg.matches(returnedPathRegex)) {
                    Log.i(TAG, "USER SELECTED <__NO__> PATH! ...");
                    return null;
                }
                excptMsg.replace(returnedPathRegex, "$1");
                Log.i(TAG, "USER SELECTED PATH: \"" + excptMsg + "\" ...");
                return excptMsg;
            }
        }

    }

    public static String selectFolderFromGUIList(@NonNull String baseDirectory, boolean basePathIsRelative) {
        return runFileChooserForResult(FileChooserBuilder.SelectionModeType.SELECT_DIRECTORY_ONLY, baseDirectory, basePathIsRelative);
    }

    public static String selectFileFromGUIList(@NonNull String baseDirectory, boolean basePathIsRelative) {
        return runFileChooserForResult(FileChooserBuilder.SelectionModeType.SELECT_FILE_ONLY, baseDirectory, basePathIsRelative);
    }

}
