package com.dev.jzw.helper.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 景占午 on 2017/9/19 0019.
 * 文件相关操作的所有操作
 * 分别有内存储和外部存储，
 * 视频缓存路劲，音频缓存路劲，图片缓存路劲等
 */

public class FileUtil {
    private static final String PROJECT_DIR = "daoshu";


    /**
     * 获取外部存储卡目录，并且创建一个名字为 PROJECT_DIR 的文件夹
     * /storage/emulated/0
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
        path = path + PROJECT_DIR;
        return path;
    }

    /**
     * 获取外部存储卡/storage/emulated/0/ PROJECT_DIR
     * 下的一个目录，如果没有就创建一个
     *
     * @param dirName
     * @return
     */
    public static String createDir(String dirName) {
        String dir = getExternalDir();
        String subDir = dir + File.separator + dirName;
        File file = new File(subDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获得外部存储卡 /Android/data/包名/cache 目录
     * 这个目录下的文件 在应用卸载时一并会删除
     *
     * @param context
     * @return
     */
    public static String getProjectCacheDir(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("没有存储卡");
        }
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 从/Android/data/包名/cache 目录 获取指定文件夹名称 的目录地址
     *
     * @param context
     * @param dirName 目录名
     * @return
     */
    public static String createDir(Context context, String dirName) {
        String cacheDir = getProjectCacheDir(context);
        String dir = cacheDir + File.separator + dirName;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取音频存储路径，/Android/data/包名/cache/audio
     *
     * @param context
     * @return
     */
    public static String getAudioDir(Context context) {
        return createDir(context, "audio");
    }

    /**
     * 获取视频存储路径，/Android/data/包名/cache/video
     *
     * @param context
     * @return
     */
    public static String getVideoDir(Context context) {
        return createDir(context, "video");
    }

    /**
     * 获取图片存储路径，/Android/data/包名/cache/pic
     *
     * @param context
     * @return
     */
    public static String getPicDir(Context context) {
        return createDir(context, "pic");
    }


    /**
     * 获取缓存目录，/Android/data/包名/cache/cache
     *
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        return createDir(context, "cache");
    }

    /**
     * 获取外部存储卡上本项目下图片的存放目录
     * /storage/emulated/0/ PROJECT_DIR /image
     *
     * @return
     */
    public static String getPicDir() {
        return createDir("image");
    }

    /**
     * 获取外部存储卡上本项目下视频的存放目录
     * /storage/emulated/0/ PROJECT_DIR /video
     *
     * @return
     */
    public static String getVideoDir() {
        return createDir("video");
    }

    /**
     * 获取外部存储卡上本项目下音频的存放目录
     * /storage/emulated/0/ PROJECT_DIR /audio
     *
     * @return
     */
    public static String getAudioDir() {
        return createDir("audio");
    }

    /**
     * 获取/storage/emulated/0/ PROJECT_DIR /cache 目录
     *
     * @return
     */
    public static String getCacheDir() {
        return createDir("cache");
    }

    /**
     * 递归删除 某一文件夹下的所有文件和子文件夹,最后删除本身
     *
     * @param dir 文件夹
     * @return
     */
    public static boolean deleteFileFromDir(String dir) {
        File file = new File(dir);
        return deleteFileFromDir(file, true);
    }

    /**
     * 递归删除文件，
     *
     * @param dir
     * @param deleteSelf 是否删除根文件夹本身
     * @return
     */
    public static boolean deleteFileFromDir(String dir, boolean deleteSelf) {
        File file = new File(dir);
        return deleteFileFromDir(file, deleteSelf);
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param dir 要删除的根目录
     */
    public static boolean deleteFileFromDir(File dir) {
        return deleteFileFromDir(dir, true);
    }

    /**
     * 递归删除文件，需要运行时 文件读写权限，否则文件删除失败
     *
     * @param dir
     * @param deleteSelf 是否删除根文件夹本身
     * @return
     */
    public static boolean deleteFileFromDir(File dir, boolean deleteSelf) {
        if (null == dir || !dir.exists()) {
            return true;
        }
        if (dir.isFile()) {
            return dir.delete();
        }
        if (dir.isDirectory()) {
            File[] childFile = dir.listFiles();
            if (childFile == null || childFile.length == 0) {
                return dir.delete();
            }
            for (File f : childFile) {
                deleteFileFromDir(f);
            }
            if (deleteSelf) {
                dir.delete();
            }
        }
        return true;
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return deleteFile(file);
    }

    /**
     * 删除一个文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (null == file || !file.exists()) {
            return true;
        }
        return file.delete();
    }

    /**
     * 删除外存储卡上的 项目目录
     * /storage/emulated/0/ PROJECT_DIR
     *
     * @return
     */
    public static boolean deleteExternalDir() {
        return deleteFileFromDir(getExternalDir());
    }

    /**
     * 删除 /Android/data/包名/下的所有目录和文件
     *
     * @param context
     */
    public static void deleteProjectCacheDir(Context context) {
        deleteAudioFile(context);
        deleteVideoFile(context);
        deletePicFile(context);
        deleteCacheFile(context);
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
     * 删除内存储卡上的应用下的视频文件夹下的所有视频文件
     *
     * @param context
     */
    public static boolean deleteVideoFile(Context context) {
        return deleteFileFromDir(getVideoDir(context));
    }

    /**
     * 删除项目包名下的 /cache/pic 目录
     *
     * @param context
     * @return
     */
    public static boolean deletePicFile(Context context) {
        String dir = getPicDir(context);
        return deleteFileFromDir(dir);
    }

    /**
     * 删除项目包名下的 /cache/cache 目录
     *
     * @param context
     * @return
     */
    public static boolean deleteCacheFile(Context context) {
        return deleteFileFromDir(getCacheDir(context));
    }

    /**
     * 删除音频 文件夹下的所有文件
     */
    public static boolean deleteAudioFile() {
        String dir = getAudioDir();
        return deleteFileFromDir(dir);
    }

    /**
     * 删除视频 文件夹下的所有文件
     */
    public static boolean deleteVideoFile() {
        String dir = getVideoDir();
        return deleteFileFromDir(dir);
    }

    /**
     * 删除存储卡上的图片文件
     *
     * @return
     */
    public static boolean deletePicFile() {
        String dir = getPicDir();
        return deleteFileFromDir(dir);
    }

    public static boolean deleteCacheFile() {
        return deleteFileFromDir(getCacheDir());
    }


    /**
     * 获取文件或者文件夹大小
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static long getFileSize(String filePath) throws Exception {
        File file = new File(filePath);
        if (null == file || !file.exists()) {
            return 0;
        }
        return getFileSize(file);
    }

    /**
     * 获取文件夹或者 文件的大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        if (null == file || !file.exists()) {
            return 0;
        }
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


    /**
     * 文件压缩
     *
     * @param srcFile 要压缩的源文件
     * @param desFile 压缩后的目标文件
     * @throws IOException
     */
    public static void zip(File srcFile, File desFile) throws IOException {
        GZIPOutputStream zos = null;
        FileInputStream fis = null;
        try {
            //创建压缩输出流,将目标文件传入
            zos = new GZIPOutputStream(new FileOutputStream(desFile));
            //创建文件输入流,将源文件传入
            fis = new FileInputStream(srcFile);
            byte[] buffer = new byte[1024];
            int len = -1;
            //利用IO流写入写出的形式将源文件写入到目标文件中进行压缩
            while ((len = (fis.read(buffer))) != -1) {
                zos.write(buffer, 0, len);
            }
        } finally {
            try {
                zos.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件解压缩
     *
     * @param srcFile 源文件
     * @param desFile 目标文件
     * @throws IOException
     */
    public static void unZip(File srcFile, File desFile) throws IOException {
        GZIPInputStream zis = null;
        FileOutputStream fos = null;
        try {
            //创建压缩输入流,传入源文件
            zis = new GZIPInputStream(new FileInputStream(srcFile));
            //创建文件输出流,传入目标文件
            fos = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int len = -1;
            //利用IO流写入写出的形式将压缩源文件解压到目标文件中
            while ((len = (zis.read(buffer))) != -1) {
                fos.write(buffer, 0, len);
            }
        } finally {
            try {
                zis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件或者目录
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void zip(String src, String dest) throws IOException {
        //定义压缩输出流
        ZipOutputStream out = null;
        try {
            //传入源文件
            File outFile = new File(dest);
            File fileOrDirectory = new File(src);
            //传入压缩输出流
            out = new ZipOutputStream(new FileOutputStream(outFile));
            //判断是否是一个文件或目录
            //如果是文件则压缩
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //否则列出目录中的所有文件递归进行压缩
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩文件或者目录
     *
     * @param out
     * @param fileOrDirectory
     * @param curPath
     * @throws IOException
     */
    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
        FileInputStream in = null;
        try {
            //判断目录是否为null
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //归档压缩目录
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                //将压缩目录写到输出流中
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                //列出目录中的所有文件
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    //递归压缩
                    zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
