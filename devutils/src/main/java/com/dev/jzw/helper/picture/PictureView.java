package com.dev.jzw.helper.picture;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dev.jzw.helper.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/3/13 0013
 * @change
 * @describe 大图查看器
 **/
public class PictureView {

    private static final byte URLS = 0; //网络查看状态
    private static final byte FILES = 1; //本地查看状态
    private byte mStatus;

    private Activity mActivity;
    private List<String> mUrls;
    private List<File> mFiles;
    private List<File> mDownloadFiles;

    private int mSelectedPosition;

    private Dialog mDialog;

    private ImageView imDelete;
    private ImageView imDownload;
    private TextView tvImageCount;
    private ViewPager mViewPager;

    private List<View> mViews;
    private PicPagerAdapter mAdapter;

    private OnDeleteItemListener mListener;
    private int mStartPosition;

    private ImageDownloader mImageDownloader;

    public PictureView(Activity activity) {
        this.mActivity = activity;
        mImageDownloader = new GlideDownloader();
        init();
    }

    public PictureView(Activity activity, ImageDownloader downloader) {
        this.mActivity = activity;
        mImageDownloader = downloader;
        init();
    }

    /**
     * 设置网络图片
     *
     * @param urls
     * @param startPosition
     */
    public void setUrls(List<String> urls, int startPosition) {
        if (mUrls == null) {
            mUrls = new ArrayList<>();
        } else {
            mUrls.clear();
        }
        mUrls.addAll(urls);
        mStatus = URLS;
        imDelete.setVisibility(View.GONE);
        if (mDownloadFiles == null) {
            mDownloadFiles = new ArrayList<>();
        } else {
            mDownloadFiles.clear();
        }
        mStartPosition = startPosition++;
        String text = startPosition + "/" + urls.size();
        tvImageCount.setText(text);
    }

    /**
     * 设置本地图片
     *
     * @param files
     * @param startPosition
     */
    public void setFiles(List<File> files, int startPosition) {
        if (mFiles == null) {
            mFiles = new LinkedList<>();
        } else {
            mFiles.clear();
        }
        mFiles.addAll(files);
        mStatus = FILES;
        imDownload.setVisibility(View.GONE);
        mStartPosition = startPosition++;
        String text = startPosition + "/" + files.size();
        tvImageCount.setText(text);
    }

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        mListener = listener;
    }

    private void init() {
        RelativeLayout relativeLayout = (RelativeLayout) mActivity.getLayoutInflater().inflate(R.layout.pic_dialog, null);
        ImageView close = relativeLayout.findViewById(R.id.scale_image_close);
        imDelete = relativeLayout.findViewById(R.id.scale_image_delete);
        imDownload = relativeLayout.findViewById(R.id.scale_image_save);
        tvImageCount = relativeLayout.findViewById(R.id.scale_image_count);
        mViewPager = relativeLayout.findViewById(R.id.scale_image_view_pager);
        mDialog = new Dialog(mActivity, R.style.Dialog_Fullscreen);
        mDialog.setContentView(relativeLayout);
        close.setOnClickListener(v -> mDialog.dismiss());
        imDelete.setOnClickListener(v -> {
            int size = mViews.size();
            mFiles.remove(mSelectedPosition);
            if (mListener != null) {
                mListener.onDelete(mSelectedPosition);
            }
            mViewPager.removeView(mViews.remove(mSelectedPosition));
            if (mSelectedPosition != size) {
                int position = mSelectedPosition + 1;
                String text = position + "/" + mViews.size();
                tvImageCount.setText(text);
            }
            mAdapter.notifyDataSetChanged();
        });
        imDelete.setOnClickListener(v -> {
            try {
                MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                        mDownloadFiles.get(mSelectedPosition).getAbsolutePath(),
                        mDownloadFiles.get(mSelectedPosition).getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Snackbar.make(mViewPager, "图片保存成功", Snackbar.LENGTH_SHORT).show();
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPosition = position;
                String text = ++position + "/" + mViews.size();
                tvImageCount.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void create() {
        mDialog.show();
        mViews = new ArrayList<>();
        mAdapter = new PicPagerAdapter(mViews, mDialog);
        if (mStatus == URLS) {
            for (String url : mUrls) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.pic_item, null);
                SubsamplingScaleImageView imageView = frameLayout.findViewById(R.id.scale_image_view);
                mViews.add(frameLayout);
                IOThread.getSingleThread().execute(() -> {
                    File downLoadFile;
                    try {
                        downLoadFile = mImageDownloader.downLoad(url, mActivity);
                        mDownloadFiles.add(downLoadFile);
                        mActivity.runOnUiThread(() -> imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            mViewPager.setAdapter(mAdapter);
        } else if (mStatus == FILES) {
            for (File file : mFiles) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.pic_item, null);
                SubsamplingScaleImageView imageView = frameLayout.findViewById(R.id.scale_image_view);
                mViews.add(frameLayout);
                imageView.setImage(ImageSource.uri(Uri.fromFile(file)));
            }
            mViewPager.setAdapter(mAdapter);
        }
        mViewPager.setCurrentItem(mStartPosition);
    }

    public interface OnDeleteItemListener {
        void onDelete(int position);
    }
}
