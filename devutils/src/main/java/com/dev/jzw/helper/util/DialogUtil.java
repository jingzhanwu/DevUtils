package com.dev.jzw.helper.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.dev.jzw.helper.widget.IosAlertDialog;


public final class DialogUtil {

    private DialogUtil() {
    }

    public static interface ConfirmListener {
        public void yes();

        public void no();
    }


    public static void confirm(Context context, int messageResId, final ConfirmListener listener) {
        confirm(context, context.getApplicationContext().getResources().getString(messageResId), listener);
    }

    /**
     * 提示对话框
     *
     * @param context
     * @param message
     * @param listener
     */
    public static void confirm(Context context, String message, final ConfirmListener listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(message).setPositiveButton("是", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.yes();
            }
        }).setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                listener.no();
            }
        }).setNegativeButton("否", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.no();
            }
        }).setTitle("系统提示");

        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        dialogBuilder.create().show();
    }

    public interface DialogClickListener {

        void yes();

        void no();

    }

    public interface DialogAlertListener {

        void yes();

    }

    /**
     * 只有确定按钮的 提示对话框
     *
     * @param act
     * @param title
     * @param message
     */
    public static void alertIosDialog(Activity act, String title, String message) {
        IosAlertDialog dialog = new IosAlertDialog(act).builder();
        dialog.setTitle(title);
        dialog.setMsg(message);
        dialog.setPositiveButton("确定", null);
        dialog.show();
    }

    /**
     * 普通的IOS 样式的提交对话框
     *
     * @param act
     * @param title
     * @param message
     * @param listener
     */
    public static void alertIosDialog(Activity act, String title, String message, final DialogClickListener listener) {

        IosAlertDialog dialog = new IosAlertDialog(act).builder();
        dialog.setTitle(title);
        dialog.setMsg(message);
        dialog.setPositiveButton("确定", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.yes();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.no();
            }
        });
        if (act != null && !act.isFinishing()) {
            dialog.show();
        }
    }

    /**
     * IOS 样式的 提交对话框
     *
     * @param act
     * @param title
     * @param message
     * @param yesButton
     * @param noButton
     * @param listener
     */
    public static void confirmIosDialog(Activity act, String title, String message, String yesButton, String noButton,
                                        final DialogClickListener listener) {
        IosAlertDialog dialog = new IosAlertDialog(act).builder();
        dialog.setTitle(title);
        dialog.setMsg(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(yesButton == null ? "确定" : yesButton, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.yes();
            }
        });
        dialog.setNegativeButton(noButton == null ? "取消" : noButton, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.no();
            }
        });
        dialog.show();
    }

}
