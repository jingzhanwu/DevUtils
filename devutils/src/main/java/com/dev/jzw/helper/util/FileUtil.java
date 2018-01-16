package com.dev.jzw.helper.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by 景占午 on 2017/9/19 0019.
 * 文件相关操作的所有操作
 */

public class FileUtil {
    private static final String PROJECT_DIR = "daoshu";

    /**
     * 获取音频存储路径，data/data/项目报名/audio
     *
     * @param context
     * @return
     */
    public static String getAudioDir(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("没有存储卡");
        }
        return getDir(context, "audio");
    }

    /**
     * 获取视频存储路径，data/data/项目报名/video
     *
     * @param context
     * @return
     */
    public static String getVideoDir(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("没有存储卡");
        }
        return getDir(context, "video");
    }

    /**
     * 获取视频存储路径，data/data/项目报名/pic
     *
     * @param context
     * @return
     */
    public static String getPicDir(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("没有存储卡");
        }
        return getDir(context, "pic");
    }

    /**
     * 获取外部存储卡上本项目的文件存储目录
     *
     * @return
     */
    public static String getExternalDir() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("没有存储卡");
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        path = path + PROJECT_DIR + File.separator;
        return path;
    }

    /**
     * 获取外部存储卡上本项目下图片的存放目录
     *
     * @return
     */
    public static String getImageDir() {
        return getDir("image");
    }

    /**
     * 获取外部存储卡上本项目下视频的存放目录
     *
     * @return
     */
    public static String getVideoDir() {
        return getDir("video");
    }

    /**
     * 获取外部存储卡上本项目下音频的存放目录
     *
     * @return
     */
    public static String getAudioDir() {
        return getDir("audio");
    }

    /**
     * 从外部存储卡上的本应用下获得一个制定文件夹的目录
     *
     * @param subDir
     * @return
     */
    public static String getDir(String subDir) {
        String path = getExternalDir();
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dir = new File(path + subDir + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    public static String getDir(Context context, String dirName) {
        String cacheDir = context.getExternalCacheDir().getAbsolutePath();
        String dir = cacheDir + File.separator + dirName;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }


    public static boolean deleteFileFromDir(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else {
                String[] filePaths = file.list();
                for (String path : filePaths) {
                    deleteFileFromDir(dir + File.separator + path);
                }
                return file.delete();
            }
        } else {
            return true;
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }


    /**
     * 获取文件大小，并转换为对应的MB  GB  KB 等
     *
     * @param path
     * @return
     */
    public static String formatFileSize(String path) {
        String fileSizeString = "";
        String wrongSize = "0B";
        try {
            long fileSize = getFileSize(new File(path));
            DecimalFormat df = new DecimalFormat("0.00");
            if (fileSize == 0) {
                return wrongSize;
            }
            if (fileSize < 1024) {
                fileSizeString = df.format((double) fileSize) + "B";
            } else if (fileSize < 1048576) {
                fileSizeString = df.format((double) fileSize / 1024) + "KB";
            } else if (fileSize < 1073741824) {
                fileSizeString = df.format((double) fileSize / 1048576) + "MB";
            } else {
                fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSizeString;
    }


    /**
     * 获取文件 或者文件夹大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    size += getFileSize(files[i]);
                }
            } else {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }
}
