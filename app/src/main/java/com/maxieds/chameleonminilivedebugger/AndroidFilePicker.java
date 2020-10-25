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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.rosuh.filepicker.bean.FileItemBeanImpl;
import me.rosuh.filepicker.config.AbstractFileDetector;
import me.rosuh.filepicker.config.AbstractFileFilter;
import me.rosuh.filepicker.config.FilePickerConfig;
import me.rosuh.filepicker.config.FilePickerManager;
import me.rosuh.filepicker.filetype.FileType;

import static com.maxieds.chameleonminilivedebugger.ExternalFileIO.CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE;
import static com.maxieds.chameleonminilivedebugger.ExternalFileIO.CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE;

public class AndroidFilePicker {

    private static final String TAG = AndroidFilePicker.class.getSimpleName();

    public static class HiddenFileType implements FileType {
        @Override
        public String getFileType() { return "Hidden"; }
        @Override
        public int getFileIconResId() {
            return R.drawable.file_picker_icon24_hidden;
        }
        @Override
        public boolean verify(String fileSpec) {
            if(!fileSpec.contains(".") || fileSpec.indexOf(".") > 0) {
                return false;
            }
            return true;
        }
    }

    public static class TextFileType implements FileType {
        private static final String fileTypeExts = "|lst|csv|txt|xml|keys|cfg|dat|";
        @Override
        public String getFileType() { return "Text"; }
        @Override
        public int getFileIconResId() {
            return R.drawable.file_picker_icon24_txt;
        }
        @Override
        public boolean verify(String fileSpec) {
            if(!fileSpec.contains(".")) {
                return false;
            }
            else {
                fileSpec = fileSpec.substring(fileSpec.lastIndexOf(".") + 1).toLowerCase();
                return fileTypeExts.contains(fileSpec + "|");
            }
        }
    }

    public static class LogFileType implements FileType {
        private static final String fileTypeExts = "|log|out|debug|dbg|run|";
        @Override
        public String getFileType() { return "Log"; }
        @Override
        public int getFileIconResId() {
            return R.drawable.file_picker_icon24_log;
        }
        @Override
        public boolean verify(String fileSpec) {
            if(!fileSpec.contains(".")) {
                return false;
            }
            else {
                fileSpec = fileSpec.substring(fileSpec.lastIndexOf(".") + 1).toLowerCase();
                return fileTypeExts.contains(fileSpec + "|");
            }
        }
    }

    public static class ScriptFileType implements FileType {
        private static final String fileTypeExts = "|sh|bat|cmd|cmld|";
        @Override
        public String getFileType() { return "Script"; }
        @Override
        public int getFileIconResId() {
            return R.drawable.file_picker_icon24_sh;
        }
        @Override
        public boolean verify(String fileSpec) {
            if(!fileSpec.contains(".")) {
                return false;
            }
            else {
                fileSpec = fileSpec.substring(fileSpec.lastIndexOf(".") + 1).toLowerCase();
                return fileTypeExts.contains(fileSpec + "|");
            }
        }
    }

    public static class BinaryFileType implements FileType {
        private static final String fileTypeExts = "|dmp|dump|hex|bin|mfd|";
        @Override
        public String getFileType() { return "Binary"; }
        @Override
        public int getFileIconResId() {
            return R.drawable.file_picker_icon24_bin;
        }
        @Override
        public boolean verify(String fileSpec) {
            if(!fileSpec.contains(".")) {
                return false;
            }
            else {
                fileSpec = fileSpec.substring(fileSpec.lastIndexOf(".") + 1).toLowerCase();
                return fileTypeExts.contains(fileSpec + "|");
            }
        }
    }

    public static final int FILE_TYPE_TEXT = 0x0001;
    public static final int FILE_TYPE_LOG = 0x0002;
    public static final int FILE_TYPE_SCRIPT = 0x0004;
    public static final int FILE_TYPE_BINARY = 0x0008;
    public static final int FILE_TYPE_HIDDEN = 0x0010;

    public static List<FileType> getFileTypesFilter(int typesMask) {
        List<FileType> typesList = new ArrayList<FileType>();
        if((typesMask & FILE_TYPE_TEXT) != 0) {
            typesList.add(new TextFileType());
        }
        if((typesMask & FILE_TYPE_LOG) != 0) {
            typesList.add(new LogFileType());
        }
        if((typesMask & FILE_TYPE_SCRIPT) != 0) {
            typesList.add(new ScriptFileType());
        }
        if((typesMask & FILE_TYPE_BINARY) != 0) {
            typesList.add(new BinaryFileType());
        }
        if((typesMask & FILE_TYPE_HIDDEN) != 0) {
            typesList.add(new HiddenFileType());
        }
        return typesList;
    }

    public static class SelectDirectoryFilter extends AbstractFileFilter {
        @Override
        public ArrayList<FileItemBeanImpl> doFilter(ArrayList<FileItemBeanImpl> fileItemsList) {
            ArrayList<FileItemBeanImpl> selectedDirFiles = new ArrayList<FileItemBeanImpl>();
            for(int itemIdx = 0; itemIdx < fileItemsList.size(); itemIdx++) {
                FileItemBeanImpl fileItem = fileItemsList.get(itemIdx);
                if(fileItem.isDir()) {
                    selectedDirFiles.add(fileItem);
                }
            }
            return selectedDirFiles;
        }
    }

    public static class RestrictFilesFilter extends AbstractFileFilter {

        private List<FileType> allowedFileTypes;
        public RestrictFilesFilter(List<FileType> inputFileTypeSpec) {
            allowedFileTypes = inputFileTypeSpec;
        }
        private FileType getAssociatedFileType(FileItemBeanImpl fileItem) {
            for(int typeIdx = 0; typeIdx < allowedFileTypes.size(); typeIdx++) {
                if(allowedFileTypes.get(typeIdx).verify(fileItem.getFileName())) {
                    return allowedFileTypes.get(typeIdx);
                }
            }
            return null;
        }

        @Override
        public ArrayList<FileItemBeanImpl> doFilter(ArrayList<FileItemBeanImpl> fileItemsList) {
            ArrayList<FileItemBeanImpl> selectedFilesByType = new ArrayList<FileItemBeanImpl>();
            for(int itemIdx = 0; itemIdx < fileItemsList.size(); itemIdx++) {
                FileItemBeanImpl fileItem = fileItemsList.get(itemIdx);
                if(getAssociatedFileType(fileItem) != null) {
                    selectedFilesByType.add(fileItem);
                }
            }
            return selectedFilesByType;
        }

    }

    public static class RestrictFilesDetector extends AbstractFileDetector {
        private List<FileType> allowedFileTypes;
        public RestrictFilesDetector(List<FileType> inputFileTypeSpec) {
            allowedFileTypes = inputFileTypeSpec;
        }
        private FileType getAssociatedFileType(FileItemBeanImpl fileItem) {
            for(int typeIdx = 0; typeIdx < allowedFileTypes.size(); typeIdx++) {
                if(allowedFileTypes.get(typeIdx).verify(fileItem.getFileName())) {
                    return allowedFileTypes.get(typeIdx);
                }
            }
            return null;
        }
        @Override
        public FileItemBeanImpl fillFileType(FileItemBeanImpl fileItem) {
            fileItem.setFileType(getAssociatedFileType(fileItem));
            return fileItem;
        }
    }

    public static final int ACTION_SELECT_DIRECTORY = 1;
    public static final int ACTION_SELECT_FILE = 2;

    public static final int CONFIG_MAX_SELECTED_FILES = 3;
    public static final String CONFIG_DEFAULT_STORAGE_TYPE = "STORAGE_EXTERNAL_STORAGE";

    private int filePickerAction;
    private boolean showHidden;
    private boolean selectMultiple;
    private String rootDirectoryPath;
    private int themeId;
    private List<FileType> displayFileTypes;

    public AndroidFilePicker() {
        filePickerAction = ACTION_SELECT_FILE;
        showHidden = false;
        selectMultiple = false;
        rootDirectoryPath = null;
        themeId = -1;
        displayFileTypes = null;
    }

    public void setAction(int fpAction) {
        filePickerAction = fpAction;
    }

    public void showHidden(boolean show) {
        showHidden = show;
    }

    public void selectMultiple(boolean multiSelect) {
        selectMultiple = multiSelect;
    }

    public void setRootPath(String rootFilePath) {
        rootDirectoryPath = rootFilePath;
    }

    public void setTheme(@IdRes int themeResId) {
        themeId = themeResId;
    }

    public void setFileTypes(int typesMask) {
        displayFileTypes = getFileTypesFilter(typesMask);
    }

    public String getFilePath(ChameleonMiniLiveDebuggerActivity activity, int forResultCode) {
        FilePickerManager fpManager = FilePickerManager.INSTANCE;
        fpManager.from(activity);
        FilePickerConfig fpConfig = new FilePickerConfig(fpManager);
        if(filePickerAction == ACTION_SELECT_DIRECTORY) {
            fpConfig.skipDirWhenSelect(false);
            fpConfig.filter(new SelectDirectoryFilter());
            fpConfig.setAutoFilter(true);
            fpConfig.setPickDirectoryAction();
        }
        else {
            fpConfig.skipDirWhenSelect(true);
            if(displayFileTypes != null) {
                fpConfig.filter(new RestrictFilesFilter(displayFileTypes));
                fpConfig.setCustomDetector(new RestrictFilesDetector(displayFileTypes));
                fpConfig.setAutoFilter(true);
            }
        }
        fpConfig.showHiddenFiles(showHidden);
        if(selectMultiple) {
            fpConfig.maxSelectable(CONFIG_MAX_SELECTED_FILES);
        }
        else {
            fpConfig.enableSingleChoice();
            fpConfig.maxSelectable(1);
        }
        if(rootDirectoryPath != null) {
            fpConfig.setCustomRootPath(rootDirectoryPath);
        }
        else {
            fpConfig.storageType(CONFIG_DEFAULT_STORAGE_TYPE);
        }
        if(themeId != -1) {
            fpConfig.setTheme(themeId);
        }
        if(displayFileTypes != null) {
            fpConfig.registerFileType(displayFileTypes, true);
        }
        fpConfig.showCheckBox(true);
        fpConfig.forResult(forResultCode);
        /* Now wait for the activityResult to invoke a RuntimeException: */
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            try {
                String selectedFilePath = rte.getMessage().replace("java.lang.RuntimeException: ", "");
                File selectedFile = new File(selectedFilePath);
                /* The next procedure is necessary because for some reason the app otherwise
                 * freezes without bringing the original Activity context back to the front:
                 */
                activity.moveTaskToBack(false);
                Intent bringToFrontIntent = new Intent(activity, activity.getClass());
                bringToFrontIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(bringToFrontIntent);
                /* Now resume to return the data we requested: */
                if(selectedFilePath.equals("")) {
                    return "";
                }
                else if(filePickerAction == ACTION_SELECT_DIRECTORY && !selectedFile.isDirectory()) {
                    Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "Selected file \"%s\" is not a directory.", selectedFilePath));
                    return "";
                }
                if(filePickerAction == ACTION_SELECT_FILE && selectedFile.isDirectory()) {
                    Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "Selected file \"%s\" is a directory.", selectedFilePath));
                    return "";
                }
                return selectedFilePath;
            } catch(Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public static String selectFolderFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        AndroidFilePicker filePicker = new AndroidFilePicker();
        filePicker.setAction(ACTION_SELECT_DIRECTORY);
        filePicker.selectMultiple(false);
        filePicker.showHidden(true);
        filePicker.setRootPath(baseDirectory);
        filePicker.setTheme(activity.getThemeId());
        filePicker.setFileTypes(0x1f);
        return filePicker.getFilePath(activity, CHOOSER_ACTIVITY_PICK_DIRECTORY_RESULT_CODE);
    }

    public static String selectTextFileFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        AndroidFilePicker filePicker = new AndroidFilePicker();
        filePicker.setAction(ACTION_SELECT_FILE);
        filePicker.selectMultiple(false);
        filePicker.showHidden(true);
        filePicker.setRootPath(baseDirectory);
        filePicker.setTheme(activity.getThemeId());
        filePicker.setFileTypes(0x1f & (~FILE_TYPE_BINARY));
        return filePicker.getFilePath(activity, CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE);
    }

    public static String selectFileFromGUIList(@NonNull ChameleonMiniLiveDebuggerActivity activity, @NonNull String baseDirectory) {
        AndroidFilePicker filePicker = new AndroidFilePicker();
        filePicker.setAction(ACTION_SELECT_FILE);
        filePicker.selectMultiple(false);
        filePicker.showHidden(true);
        filePicker.setRootPath(baseDirectory);
        filePicker.setTheme(activity.getThemeId());
        filePicker.setFileTypes(0x1f);
        return filePicker.getFilePath(activity, CHOOSER_ACTIVITY_PICK_FILE_RESULT_CODE);
    }

}
