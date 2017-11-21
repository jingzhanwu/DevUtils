package com.dev.jzw.helper.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * Created by 景占午 on 2016/4/13.
 * 图片加载的封装类，使用时直接使用这类里面的方法即可，不用额外配置
 * 其他选项，
 */
public class GlideUtils {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static RequestOptions requestOptions() {
        RequestOptions options = new RequestOptions();
        options.error(0)
                .placeholder(0)
                .priority(Priority.HIGH)//图片加载的优先级
                .centerCrop();
        return options;
    }

    /**
     * @param context
     * @param url
     * @param imageView
     */
    public static void loadImag(Context context, String url, ImageView imageView) {
        Glide.with(context.getApplicationContext()).load(url)
                .apply(requestOptions())
                .into(imageView);
    }

    public static void loadImagByUrl(Context context, String url, ImageView imageView) {
        Glide.with(context.getApplicationContext()).load(url)
                .apply(requestOptions())
                .into(imageView);
    }

    public static void loadImagByUri(Context context, Uri uri, ImageView imageView) {
        Glide.with(context.getApplicationContext()).load(uri)
                .apply(requestOptions())
                .into(imageView);
    }

    public static void loadImagByFile(Context context, File file, ImageView imageView) {
        Glide.with(context.getApplicationContext()).load(file)
                .apply(requestOptions()).into(imageView);
    }

    public static void loadImagByResource(Context context, int resourceId, ImageView imageView) {
        Uri uri = resourceIdToUri(context.getApplicationContext(), resourceId);
        Glide.with(context.getApplicationContext()).load(uri)
                .apply(requestOptions()).into(imageView);
    }

    private static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getApplicationContext().getPackageName() + FOREWARD_SLASH + resourceId);
    }
}
