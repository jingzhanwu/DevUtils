package com.dev.jzw.helper.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
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
     * 获取图片存储路径，data/data/项目报名/pic
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

    /**
     * 从内存储卡上获取指定文件夹名称 的目录地址
     *
     * @param context
     * @param dirName 目录名
     * @return
     */
    public static String getDir(Context context, String dirName) {
        String cacheDir = context.getExternalCacheDir().getAbsolutePath();
        String dir = cacheDir + File.separator + dirName;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }


    /**
     * 递归删除 某一文件夹下的所有文件和子文件夹
     *
     * @param dir 文件夹
     * @return
     */
    public static boolean deleteFileFromDir(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFileFromDir(children[i]);
                    if (!success) {
                        return false;
                    }
                }
            }
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 删除音频 文件夹下的所有文件
     */
    public static boolean deleteAudioFile() {
        String dir = getAudioDir();
        return deleteFileFromDir(dir);
    }

    /**
     * 删除内存储卡上的应用下的音频文件夹下的所有音频文件
     *
     * @param context
     */
    public static boolean deleteAudioFile(Context context) {
        return deleteFileFromDir(getAudioDir(context));
    }

    /**
     * 删除视频 文件夹下的所有文件
     */
    public static boolean deleteVideoFile() {
        String dir = getVideoDir();
        return deleteFileFromDir(dir);
    }

    /**
     * 删除内存储卡上的应用下的视频文件夹下的所有视频文件
     *
     * @param context
     */
    public static boolean deleteVideoFile(Context context) {
        return deleteFileFromDir(getVideoDir(context));
    }


    /**
     * 获取文件 或者 文件的大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFileSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String formatFileSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
