package com.dev.jzw.helper.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * APP 6.0 及以上系统的权限管理类
 * Created by 景占午 on 2017/6/15.
 */

public class PermissionUtil {
    public final static int SETTION_REQUEST_CODE = 1;
    public final static int RECORD_REQUESTCODE = 2;
    public static final int LOCATION_REQUESTCODE = 3;
    public static final int READ_WRITE_REQUESTCODE = 4;

    /**
     * APP sdcard 读写权限
     */
    private static final String[] readWritePermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    /**
     * APP 视频录制权限
     */
    private static final String[] recordPermissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * APP 定位权限
     */
    private static final String[] locationPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    /**
     * 检查是否有需要授权的权限
     * 如果有没有开启的权限则返回去开启权限
     *
     * @param
     * @since 2.5.0
     */
    public static String[] checkPremission(Context context, String[] permissions) {
        List<String> needPermission = findDeniedPermissions(context, permissions);
        if (needPermission != null && needPermission.size() > 0) {
            return needPermission.toArray(new String[needPermission.size()]);
        }
        return null;
    }

    /**
     * 检查定位相关权限
     *
     * @param context
     * @return
     */
    public static String[] checkLocationPermission(Context context) {
        return checkPremission(context, locationPermissions);
    }

    /**
     * 检查音视频录制权限
     *
     * @param context
     * @return
     */
    public static String[] checkRecordPermission(Context context) {
        return checkPremission(context, recordPermissions);
    }

    /**
     * 检查读写权限
     *
     * @param context
     * @return
     */
    public static String[] checkReadWritePermission(Context context) {
        return checkPremission(context, readWritePermissions);
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private static List<String> findDeniedPermissions(Context context, String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }


    /**
     * 检测权限组的权限是否全部授权成功
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
