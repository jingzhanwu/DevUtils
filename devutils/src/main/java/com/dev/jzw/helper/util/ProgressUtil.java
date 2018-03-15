package com.dev.jzw.helper.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.dev.jzw.helper.R;

import java.lang.ref.WeakReference;

/**
 * 加载对话框 工具类
 * 负责对话框的显示 隐藏等操作,Context 使用弱应用保存，防止内存溢出。
 */

public class ProgressUtil {

    private static ProgressDialog mProgressDialog;
    private static WeakReference<Activity> mReference = null;

    /**
     * 显示一个对话框
     *
     * @param activity
     * @return
     */
    public static ProgressDialog showLoadding(Activity activity) {
        return showLoadding(activity, null);
    }

    /**
     * 负责创建一个ProgressDialog，并显示。
     *
     * @param activity
     * @param msg
     * @return
     */
    public static ProgressDialog showLoadding(Activity activity, String msg) {
        mReference = new WeakReference<>(activity);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return mProgressDialog;
        }
        mProgressDialog = new ProgressDialog(mReference.get(), R.style.jzw_progress_dialog);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                hideLoadding();
            }
        });
        mProgressDialog.setMessage(TextUtils.isEmpty(msg) ? "请稍候..." : msg);
        mProgressDialog.show();
        return mProgressDialog;
    }

    /**
     * 隐藏对话框，并且释放相关资源
     */
    public static void hideLoadding() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
        if (mReference != null) {
            mReference.clear();
            mReference = null;
        }
    }
}
