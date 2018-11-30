package com.dev.jzw.helper.picture;

import android.app.Activity;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/3/13 0013
 * @change
 * @describe Glide 下载器
 **/
public class GlideDownloader implements ImageDownloader<File> {
    @Override
    public File downLoad(String url, Activity activity) {
        File file = null;
        try {
            file = Glide.with(activity.getApplicationContext()).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
