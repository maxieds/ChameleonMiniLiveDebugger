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

import android.content.Intent;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.maxieds.androidfilepickerlightlibrary.BasicFileProvider;
import com.maxieds.androidfilepickerlightlibrary.CustomThemeBuilder;
import com.maxieds.androidfilepickerlightlibrary.FileChooserBuilder;
import com.maxieds.androidfilepickerlightlibrary.FileUtils;

import java.util.Locale;

public class AndroidFileChooser {

    private static final String TAG = AndroidFileChooser.class.getSimpleName();

    private static final int CMLD_PICKER_ACTION_SWIZZLE_CODE = (42691 >> 1) + 3;
    public static final int ACTION_SELECT_DIRECTORY_ONLY = 1 + CMLD_PICKER_ACTION_SWIZZLE_CODE;
    public static final int ACTION_SELECT_FILE_ONLY = 2 + CMLD_PICKER_ACTION_SWIZZLE_CODE;

    public static final FileChooserBuilder.BaseFolderPathType CONFIG_DEFAULT_STORAGE_TYPE = FileChooserBuilder.BaseFolderPathType.BASE_PATH_DEFAULT;

    public static String getFileNotifySelectExceptionFormat() {
        return "PATHSELECTION:==%s";
    }

    private static final String PATH_SEP = "/";
    private static final String STORAGE_HOME_PREFIX_SUBST = "\\$\\{EXT\\}" + PATH_SEP;
    public static final String NULL_FILE_PATH_LABEL = "\\.//";

    public static String getFileChooserBaseFolder(String startingRelPath) {
        BasicFileProvider fpInst = BasicFileProvider.getInstance();
        String fileProviderCwd = fpInst.getCWD();
        int trailingPathIndex = fileProviderCwd.lastIndexOf(startingRelPath);
        if(trailingPathIndex >= 0) {
            return fileProviderCwd.substring(0, trailingPathIndex);
        }
        return fileProviderCwd;
    }

    public static String getInitialFileChooserBaseFolder() {
        BasicFileProvider fpInst = BasicFileProvider.getInstance();
        if(fpInst == null) {
            return NULL_FILE_PATH_LABEL;
        }
        fpInst.selectBaseDirectoryByType(CONFIG_DEFAULT_STORAGE_TYPE);
        String fpCwd = fpInst.getCWD();
        fpInst.resetBaseDirectory();
        return fpCwd;
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

    public static final int CMLD_PERMGROUP_STORAGE_REQUEST_CODE = 0x00FA;

    public static String runFileChooserForResult(FileChooserBuilder.SelectionModeType selectionMode, @NonNull String baseDirectory, boolean basePathIsRelative) {

        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        if(!llActivity.checkPermissionsAcquired(ActivityPermissions.CMLD_PERMISSIONS_GROUP_STORAGE)) {
            Utils.displayToastMessageShort("CMLD does not have storage permissions to access the filesystem.");
            Intent userRequestStoragePermsIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            llActivity.startActivityForResult(userRequestStoragePermsIntent, CMLD_PERMGROUP_STORAGE_REQUEST_CODE);
            return "";
        }

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

        while(true) {
            try {
                Looper.loop();
            } catch(RuntimeException ie) {
                String excptMsg = ie.getMessage();
                String replaceRegex = String.format(Locale.getDefault(), getFileNotifySelectExceptionFormat(), "");
                String[] excptMsgComponents = excptMsg.split(replaceRegex);
                if(excptMsgComponents.length != 2) {
                    Log.i(TAG, "USER SELECTED <__NO__> PATH! ... " + ie.getMessage());
                    return NULL_FILE_PATH_LABEL;
                }
                excptMsg = excptMsgComponents[1];
                if(excptMsg.length() == 0) {
                    Log.i(TAG, "USER SELECTED <__EMPTY__> PATH! ... " + ie.getMessage());
                    return NULL_FILE_PATH_LABEL;
                }
                String fileChooserBaseFolder = getInitialFileChooserBaseFolder();
                excptMsg = excptMsg.replaceFirst(fileChooserBaseFolder, STORAGE_HOME_PREFIX_SUBST);
                excptMsg = excptMsg.replaceAll(String.format(Locale.getDefault(), "[%s]+", PATH_SEP), "/");
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

    public static boolean isFileContentsTextBased(String filePath) {
        filePath = filePath.replaceFirst(STORAGE_HOME_PREFIX_SUBST, "");
        try {
            BasicFileProvider.DocumentPointer docRef = new BasicFileProvider.DocumentPointer(CONFIG_DEFAULT_STORAGE_TYPE, FileUtils.getFileBasePath(filePath));
            if (!docRef.isValid()) {
                return false;
            } else if (!docRef.locateDocument(filePath)) {
                return false;
            }
            String fileMimeType = docRef.getDocumentType();
            Log.i(TAG, "MIME TYPE: " + fileMimeType);
            return fileMimeType.toLowerCase(Locale.getDefault()).startsWith("text");
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String getFileContentsAsString(String filePath) {
        if(!isFileContentsTextBased(filePath)) {
            return null;
        }
        filePath = filePath.replaceFirst(STORAGE_HOME_PREFIX_SUBST, "");
        try {
            BasicFileProvider.DocumentPointer docRef = new BasicFileProvider.DocumentPointer(CONFIG_DEFAULT_STORAGE_TYPE, FileUtils.getFileBasePath(filePath));
            if (!docRef.isValid()) {
                return null;
            } else if (!docRef.locateDocument(filePath)) {
                return null;
            }
            return docRef.readFileContentsAsString().toString();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
