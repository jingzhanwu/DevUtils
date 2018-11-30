package com.dev.jzw.helper.util;

import android.content.Context;
import android.widget.Toast;

/**
 * tost的管理类，负责toast的弹出和隐藏
 * Created by 景占午 on 2017/9/18 0018.
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, int resid) {
        String msg = context.getResources().getString(resid);
        showToast(context, msg);
    }

    public static void showToast(Context context, String msg) {
        if (buildToast(context, msg) != null) {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showToastLong(Context context, String msg) {
        if (buildToast(context, msg) != null) {
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private static Toast buildToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        return toast;
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
