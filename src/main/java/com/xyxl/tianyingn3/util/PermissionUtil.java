package com.xyxl.tianyingn3.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2017/11/15 15:36
 * Version : V1.0
 * Introductions :
 */

public class PermissionUtil {

    //获取位置权限是否开启，蓝牙同样需要
    public static boolean RequestLocationPermission(Context c)
    {
        //获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
