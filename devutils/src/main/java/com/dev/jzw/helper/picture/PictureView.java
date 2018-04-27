package com.dev.jzw.helper.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/3/13 0013
 * @change
 * @describe 大图查看器
 **/
public class PictureView {
    private Context mContext;
    private int mStartPosition;
    private static PictureView mInstance;
    private List<String> mUrls;
    /**
     * 是否开启 下载按钮功能  默认不开启
     */
    private boolean mEnableDownload;
    private boolean mEnableDelete;

    private OnDeleteItemListener mCallback;

    public static PictureView with(Activity activity) {
        get();
        mInstance.mContext = activity;
        return mInstance;
    }

    public static PictureView get() {
        if (mInstance == null) {
            synchronized (PictureView.class) {
                if (mInstance == null) {
                    mInstance = new PictureView();
                }
            }

        }
        return mInstance;
    }

    private PictureView() {
    }

    /**
     * 设置删除监听器
     *
     * @param callback
     * @return
     */
    public PictureView setOnDeleteItemListener(OnDeleteItemListener callback) {
        mCallback = callback;
        return mInstance;
    }

    /**
     * 设置图片
     *
     * @param urls
     * @param startPosition
     */
    public PictureView setUrls(@NonNull List<String> urls, int startPosition) {
        mUrls = urls;
        mStartPosition = startPosition++;
        return mInstance;
    }

    /**
     * 打开下载按钮
     *
     * @param download
     * @return
     */
    public PictureView enableDownload(boolean download) {
        mEnableDownload = download;
        return mInstance;
    }

    /**
     * 打开删除按钮
     *
     * @param delete
     * @return
     */
    public PictureView enableDelete(boolean delete) {
        mEnableDelete = delete;
        return mInstance;
    }

    /**
     * 删除调用
     *
     * @param position
     */
    public void onDelete(int position) {
        if (mCallback != null) {
            mCallback.onDelete(position);
        }
    }

    public PictureView create() {

        Intent intent = new Intent(mContext, PictureActivity.class);
        intent.putStringArrayListExtra("url", (ArrayList<String>) mUrls);
        intent.putExtra("position", mStartPosition);
        intent.putExtra("download", mEnableDownload);
        intent.putExtra("delete", mEnableDelete);
        mContext.startActivity(intent);
        return mInstance;
    }
}
