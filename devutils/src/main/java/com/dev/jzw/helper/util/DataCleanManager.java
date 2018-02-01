package com.dev.jzw.helper.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @anthor created by jingzhanwu
 * @date 2018/2/1 0001
 * @change
 * @describe APP 缓存、应用数据管理类
 * 比如缓存清楚，缓存计算等
 **/
public class DataCleanManager {
    /**
     * 获取缓存大小
     * Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，
     * 一般放一些长时间保存的数据
     * Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，
     * 一般存放临时缓存数据
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = FileUtil.getFileSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //cache目录下
            cacheSize += FileUtil.getFileSize(context.getExternalCacheDir());
            //自定义 andio 目录下
            cacheSize += FileUtil.getFileSize(new File(FileUtil.getAudioDir(context)));
            //自定义 video 目录下
            cacheSize += FileUtil.getFileSize(new File(FileUtil.getVideoDir(context)));
            //自定义 pic 目录下
            cacheSize += FileUtil.getFileSize(new File(FileUtil.getPicDir(context)));
        }
        return FileUtil.formatFileSize(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param context
     * @throws Exception
     */
    public static void clearAllCache(Context context) throws Exception {
        FileUtil.deleteFileFromDir(context.getCacheDir().getAbsolutePath());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileUtil.deleteFileFromDir(context.getExternalCacheDir().getAbsolutePath());

            FileUtil.deleteFileFromDir(FileUtil.getAudioDir(context));
            FileUtil.deleteFileFromDir(FileUtil.getVideoDir(context));
            FileUtil.deleteFileFromDir(FileUtil.getPicDir(context));
        }
    }

}
