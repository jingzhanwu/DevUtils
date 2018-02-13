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
        long cacheSize = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //计算 data/data/包名/cache
            cacheSize += FileUtil.getFileSize(context.getCacheDir());
            //计算 data/data/包名/files
            cacheSize += FileUtil.getFileSize(context.getFilesDir());
            //获取外部存储卡项目目录大小
            cacheSize += FileUtil.getFileSize(FileUtil.getExternalDir());
            //获取/Android/data/包名/cache目录下的文件大小
            cacheSize += FileUtil.getFileSize(FileUtil.getProjectCacheDir(context));
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
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileUtil.deleteExternalDir();
            FileUtil.deleteProjectCacheDir(context);
            FileUtil.deleteFileFromDir(context.getCacheDir(), false);
            FileUtil.deleteFileFromDir(context.getFilesDir(), false);
        }
    }
}
