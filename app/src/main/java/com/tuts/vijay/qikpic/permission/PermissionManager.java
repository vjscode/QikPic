package com.tuts.vijay.qikpic.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by vijay on 2/24/16.
 */
public class PermissionManager {
    public static final int QIKPIC_PERMISSIONS_REQUEST_STORAGE = 1;
    public static final int QIKPIC_PERMISSIONS_REQUEST_LOCATION = 2;

    public static boolean checkAndAskPermission(Activity context, String permission) {
        if(ContextCompat.checkSelfPermission(context,
            permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static void askPermission(Activity context, String permission, int requestCode) {
        ActivityCompat.requestPermissions(context,
                new String[]{permission},
                requestCode);
    }
}
