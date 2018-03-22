package com.dev.jzw.helper.picture;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.jzw.helper.R;
import com.dev.jzw.helper.picture.zoom.PhotoView;
import com.dev.jzw.helper.util.GlideUtils;

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

    private static PictureView mInstance;

    public static PictureView with(Activity activity) {
        return with(activity, new GlideDownloader());
    }

    public static PictureView with(Activity activity, ImageDownloader downloader) {
        get();
        mInstance.mActivity = activity;
        mInstance.mImageDownloader = downloader;
        mInstance.init();
        return get();
    }

    private static PictureView get() {
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
     * 设置网络图片
     *
     * @param urls
     * @param startPosition
     */
    public PictureView setUrls(@NonNull List<String> urls, int startPosition) {
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
        return mInstance;
    }

    /**
     * 设置本地图片
     *
     * @param files
     * @param startPosition
     */
    public PictureView setFiles(@NonNull List<File> files, int startPosition) {
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

        return mInstance;
    }

    public PictureView setOnDeleteItemListener(OnDeleteItemListener listener) {
        mListener = listener;
        return this;
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

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFiles.size() <= 0) {
                    mDialog.dismiss();
                    return;
                }
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
            }
        });

        imDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                            mDownloadFiles.get(mSelectedPosition).getAbsolutePath(),
                            mDownloadFiles.get(mSelectedPosition).getName(), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Snackbar.make(mViewPager, "图片保存成功", Snackbar.LENGTH_SHORT).show();
            }
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

    public PictureView create() {
        mDialog.show();
        mViews = new ArrayList<>();
        mAdapter = new PicPagerAdapter(mViews, mDialog);
        if (mStatus == URLS) {
            for (final String url : mUrls) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.pic_item, null);
                final PhotoView imageView = frameLayout.findViewById(R.id.scale_image_view);
                mViews.add(frameLayout);

                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                IOThread.getSingleThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        final File downLoadFile;
                        try {
                            downLoadFile = mImageDownloader.downLoad(url, mActivity);
                            mDownloadFiles.add(downLoadFile);

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GlideUtils.loadImagByFile(mActivity.getApplicationContext()
                                            , downLoadFile, imageView);
                                    // imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile)));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            mViewPager.setAdapter(mAdapter);
        } else if (mStatus == FILES) {
            for (File file : mFiles) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.pic_item, null);
                PhotoView imageView = frameLayout.findViewById(R.id.scale_image_view);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mViews.add(frameLayout);
                GlideUtils.loadImagByFile(mActivity.getApplicationContext(),
                        file, imageView);
            }
            mViewPager.setAdapter(mAdapter);
        }
        mViewPager.setCurrentItem(mStartPosition);

        return mInstance;
    }

    public interface OnDeleteItemListener {
        void onDelete(int position);
    }
}
