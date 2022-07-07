package com.maxieds.chameleonminilivedebugger;

import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public interface ActivityPermissions {

    public static final String CMLD_PERMISSIONS_GROUP_MINIMAL = "CMLD.permission-group.MINIMAL_APP_REQUIREMENTS";
    public static final String CMLD_PERMISSIONS_GROUP_STORAGE = "CMLD.permission-group.FILES_AND_STORAGE";
    public static final String CMLD_PERMISSIONS_GROUP_BLUETOOTH = "CMLD.permission-group.BLUETOOTH_COMPENDIA";

    public static final int CMLD_PERMGROUP_MINIMAL_REQUEST_CODE = 0xFF01;
    public static final int CMLD_PERMGROUP_STORAGE_REQUEST_CODE = 0x00FA;
    public static final int CMLD_PERMS_ALL_REQUEST_CODE = 0xFFAB;

    public static Map<String,View> REQUEST_QUEUE = new LinkedHashMap<String,View>();
    public static final Semaphore PERMS_REQUEST_QUEUE_LOCK = new Semaphore(1, true);
    public static final int REQUEST_QUEUE_KEY_RADIX = 16;

    /* Wait 0.5 seconds, and bail if cannot acquire the lock */
    public static final int REQUEST_RESULT_MAX_TIMEOUT = 500;
    public static final int REQUEST_RESULT_MAX_VIEWOBJ_WAIT_TIMEOUT = 500;

    public static boolean acquireRequestQueueLock(int waitTimeout) {
        try {
            if (waitTimeout == 0) {
                return PERMS_REQUEST_QUEUE_LOCK.tryAcquire();
            } else if(waitTimeout < 0) {
                PERMS_REQUEST_QUEUE_LOCK.acquireUninterruptibly();
                return true;
            } else {
                return PERMS_REQUEST_QUEUE_LOCK.tryAcquire(waitTimeout, TimeUnit.MILLISECONDS);
            }
        } catch(Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return false;
    }

    public static boolean acquireRequestQueueLock() {
        return acquireRequestQueueLock(REQUEST_RESULT_MAX_TIMEOUT);
    }

    public static int addToRequestQueue(@NonNull View objToNotify, int waitTimeout) {
        if(!acquireRequestQueueLock(waitTimeout)) {
            return -1;
        }
        String objIdKey = Utils.hashObjectToString(objToNotify, REQUEST_QUEUE_KEY_RADIX);
        REQUEST_QUEUE.put(objIdKey, objToNotify);
        PERMS_REQUEST_QUEUE_LOCK.release();
        return resolveIntegerIDFromKey(objIdKey);
    }

    public static int addToRequestQueue(@NonNull View objToNotify) {
        return addToRequestQueue(objToNotify, REQUEST_RESULT_MAX_TIMEOUT);
    }

    public static String resolveRequestCodeKey(int requestCode) {
        try {
            return Integer.toString(requestCode, REQUEST_QUEUE_KEY_RADIX);
        } catch(Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return "";
    }

    public static int resolveIntegerIDFromKey(@NonNull String key) {
        try {
            return (int) Integer.parseInt(key, REQUEST_QUEUE_KEY_RADIX);
        } catch(Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return 0;
    }

    public String[] getPermissionsByGroup(String groupName);
    public boolean checkPermissionsAcquired(@NonNull String[] permissions, boolean requestIfNot, View viewToNotify);
    public boolean checkPermissionsAcquired(@NonNull String[] permissions, boolean requestIfNot);
    public boolean checkPermissionsAcquired(@NonNull String[] permissions);
    public boolean checkPermissionsAcquired(@NonNull String groupName, boolean requestIfNot, View viewToNotify);
    public boolean checkPermissionsAcquired(@NonNull String groupName, boolean requestIfNot);
    public boolean checkPermissionsAcquired(@NonNull String groupName);
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
