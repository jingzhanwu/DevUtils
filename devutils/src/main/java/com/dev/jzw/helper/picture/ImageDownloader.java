package com.dev.jzw.helper.picture;

import android.app.Activity;
import android.graphics.Bitmap;

import java.io.File;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/3/13 0013
 * @change
 * @describe 下载图片的抽象接口
 **/
public interface ImageDownloader {
    File downLoad(String url, Activity activity);
}
