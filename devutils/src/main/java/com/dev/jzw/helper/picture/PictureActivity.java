package com.dev.jzw.helper.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dev.jzw.helper.R;
import com.dev.jzw.helper.util.BitmapUtil;
import com.dev.jzw.helper.util.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by Administrator
 * @date 2018/4/26 0026
 * @change
 * @describe describe
 **/
public class PictureActivity extends AppCompatActivity {
    //图片最大尺寸
    private int screenW = 720;
    private int screenH = 1080;
    private ImageView imDownload;
    private ImageView ivDelete;
    private TextView tvImageCount;
    private ViewPager mViewPager;

    private List<String> mUrls;
    private int mSelectedPosition;
    private PicPagerAdapter mAdapter;
    private int mStartPosition;
    protected PictureDialog dialog;

    private static final byte URLS = 0; //网络查看状态
    private static final byte FILES = 1; //本地查看状态
    private byte mStatus;
    /**
     * 是否开启 下载按钮功能  默认不开启
     */
    private boolean mEnableDownload;
    private boolean mEnableDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_dialog);
        imDownload = findViewById(R.id.scale_image_save);
        ivDelete = findViewById(R.id.scale_image_delete);
        tvImageCount = findViewById(R.id.scale_image_count);
        mViewPager = findViewById(R.id.scale_image_view_pager);

        mUrls = getIntent().getStringArrayListExtra("url");
        mStartPosition = getIntent().getIntExtra("position", 0);
        mEnableDownload = getIntent().getBooleanExtra("download", false);
        mEnableDelete = getIntent().getBooleanExtra("delete", false);
        initPicture();
        setListener();
    }

    private void setUrlStatus() {
        if (mUrls != null) {
            String url = mUrls.get(0);
            if (url.startsWith("http://") || url.startsWith("https://")) {
                mStatus = URLS;
            } else {
                mStatus = FILES;
            }
        }
    }

    /**
     * 设置图片
     */
    public void initPicture() {
        if (mUrls == null || mUrls.size() <= 0) {
            ToastUtil.showToast(getApplicationContext(), "暂无图片");
            finish();
            return;
        }
        setUrlStatus();
        imDownload.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
        int position = mStartPosition + 1;
        String text = position + "/" + mUrls.size();
        tvImageCount.setText(text);

        mAdapter = new PicPagerAdapter(this);
        ivDelete.setVisibility(mEnableDelete ? View.VISIBLE : View.GONE);
        if (mStatus == URLS) {
            imDownload.setVisibility(mEnableDownload ? View.VISIBLE : View.GONE);
        }
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mStartPosition);
    }

    private void setListener() {
        findViewById(R.id.scale_image_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.a3);
            }
        });
        imDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(mSelectedPosition);
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUrls == null || mUrls.size() <= 0) {
                    onBackPressed();
                    return;
                }
                mUrls.remove(mSelectedPosition);
                //回调删除
                PictureView.get().onDelete(mSelectedPosition);
                if (mUrls.size() == 0) {
                    onBackPressed();
                    return;
                }
                int position = mSelectedPosition + 1;
                String text = position + "/" + mUrls.size();
                tvImageCount.setText(text);
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPosition = position;
                String text = ++position + "/" + mUrls.size();
                tvImageCount.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void downloadFile(int position) {
        String url = mUrls.get(position);
        Glide.with(PictureActivity.this.getApplicationContext())
                .asFile()
                .load(url)
                .into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(final File resource, Transition<? super File> transition) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MediaStore.Images.Media.insertImage(PictureActivity.this.getContentResolver(),
                                            resource.getAbsolutePath(),
                                            resource.getName(), null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                ToastUtil.showToast(PictureActivity.this, "图片保存成功");
                            }
                        });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.a3);
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureDialog(this);
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayImage(PhotoView imageView, Bitmap bmp) {
        try {
            if (bmp != null) {
                int w = bmp.getWidth();
                int h = bmp.getHeight();
                if (w > screenW || h > screenH) {
                    String filePath = BitmapUtil.saveBitmap(bmp);
                    Bitmap bitmap = BitmapUtil.decodeScaleImage(filePath, screenW, screenH);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    imageView.setImageBitmap(bmp);
                }
                bmp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }
    }

    private void displayNetImage(String url, final PhotoView imageView) {
        if (!TextUtils.isEmpty(url)) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this.getApplicationContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            showPleaseDialog();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            dismissDialog();
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            displayImage(imageView, resource);
                            dismissDialog();
                        }
                    });

        }

    }

    protected class PicPagerAdapter extends PagerAdapter {
        private Context context;

        PicPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = LayoutInflater.from(context).inflate(R.layout.pic_item, container, false);
            final PhotoView imageView = contentView.findViewById(R.id.scale_image_view);
            imageView.enable();
            String url = mUrls.get(position);
//            if (mStatus == URLS) {
//                showPleaseDialog();
//            }
            displayNetImage(url, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            container.addView(contentView, 0);
            return contentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position == 0 && mUrls.size() == 0) {
                return;
            }
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
