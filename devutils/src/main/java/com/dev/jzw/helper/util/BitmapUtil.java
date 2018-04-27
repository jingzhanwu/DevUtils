package com.dev.jzw.helper.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * bitmap的处理工具类
 * 包括了bitmap的压缩 压缩 裁剪等
 * <p/>
 * decode类方法 对bitmap比进行压缩，但是会根据屏幕像素密度进行合适的压缩显示
 * compress类方法 对超过制定大小的bitmap压缩，同时也会根据屏幕像素密度进行合适的压缩显示
 * Created by 景占午 on 2016/8/29.
 */
public class BitmapUtil {
    private static final int DEFAULT_DENSITY = 240;
    private static final float SCALE_FACTOR = 0.75f;
    private static final Bitmap.Config DEFAUL_BITMAP_CONFIG = Bitmap.Config.RGB_565;


    /**
     * 从文件从获取Bitmap，并压缩到指定的大小
     *
     * @param filePath
     * @param outWidth  输出图片的宽
     * @param outHeight 输出图片的高
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int outWidth, int outHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(filePath, options);
        // 获取尺寸压缩倍数
        options.inSampleSize = getRatioSize(options.outWidth, options.outHeight, outWidth, outHeight);

        return loadBitmap2File(options, filePath);
    }

    /**
     * 从本地文件获取一个Bitmap
     *
     * @param filePath
     * @param inSampleSize 压缩比
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return loadBitmap2File(options, filePath);
    }

    /**
     * 从本地文件获取一个Bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        return loadBitmap2File(options, filePath);
    }


    /**
     * 从本地文件中加载一个bitmap，并且进行角度的旋转校正
     */
    private static Bitmap loadBitmap2File(BitmapFactory.Options newOpts, String filePath) {
        newOpts.inJustDecodeBounds = false;//读取所有内容
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        File file = new File(filePath);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
                //旋转图片
                int photoDegree = readPictureDegree(filePath);
                if (photoDegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(photoDegree);
                    // 创建新的图片
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 从指定的文件中获取bitmap并压缩，压缩的是整个bimap的大小，占用内存和存储大小都改变了
     *
     * @param filePath
     * @return
     */
    public static String compressBitmap(String filePath) {
        return compressBitmap(filePath, 2);
    }

    /**
     * 从指定的文件中获取bitmap并压缩，压缩的是整个bimap的大小，占用内存和存储大小都改变了
     *
     * @param filePath
     * @return
     */
    public static String compressBitmap(String filePath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap bmp = loadBitmap2File(options, filePath);
        return saveBitmap(bmp);
    }

    /**
     * 压缩bitmap bitmap占用内存大小不变，改变的只是存储大小
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        //循环判断如果压缩后的大小是否大于300k
        while ((baos.toByteArray().length / 1024) > 300) {
            int subtract = setSubstractSize(baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            quality -= subtract;//每次都减少10
            if (quality <= 0) {
                quality = subtract;
            }
            //这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        return bitmap;
    }

    /**
     * 根据图片的大小设置压缩的比例，提高速度
     *
     * @param imageMB
     * @return
     */
    private static int setSubstractSize(int imageMB) {

        if (imageMB > 1000) {
            return 40;
        } else if (imageMB > 750) {
            return 30;
        } else if (imageMB > 500) {
            return 20;
        } else {
            return 10;
        }
    }


    /**
     * 保存一个bitmap 到指定的文件
     *
     * @param fileName 文件的全名
     * @param bitmap
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String fileName) {
        if (bitmap == null || TextUtils.isEmpty(fileName)) {
            return "";
        }
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将bitmap 保存到指定的文件路径下
     *
     * @param bmp
     * @return
     */
    public static String saveBitmap(Bitmap bmp) {
        long dataTake = System.currentTimeMillis();
        String fileName = FileUtil.getPicDir() + File.separator + "picture_" + dataTake + ".jpg";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        return saveBitmap(bmp, fileName);
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 计算图片缩放所需要的 采样率大小
     *
     * @param outWidth  原图的宽
     * @param outHeight 原图的高
     * @param outWidth  要输出的宽度
     * @param outHeight 要输出的高度
     * @return
     */
    public static int getRatioSize(int width, int height, int outWidth, int outHeight) {
        //采样率的缩放率，，为2的倍数  值越大，图片越小
        int initSize = 1;
        if (height > outHeight || width > outWidth) {
            if (width > height) {
                initSize = Math.round((float) height / (float) outHeight);
            } else {
                initSize = Math.round((float) width / (float) outWidth);
            }
        }
        /*
         * the function rounds up the sample size to a power of 2 or multiple of 8 because
         * BitmapFactory only honors sample size this way. For example, BitmapFactory
         * down samples an image by 2 even though the request is 3.
         */
        int inSampleSize;
        if (initSize <= 8) {
            inSampleSize = 1;
            while (inSampleSize < initSize) {
                inSampleSize <<= 1;
            }
        } else {
            inSampleSize = (initSize + 7) / 8 * 8;
        }
        return inSampleSize;
    }

    /**
     * 1、BitmapConfig的配置
     * 2、使用decodeFile、decodeResource、decodeStream进行解析Bitmap时，配置inDensity和inTargetDensity，两者应该相等,值可以等于屏幕像素密度*0.75f
     * 3、使用inJustDecodeBounds预判断Bitmap的大小及使用inSampleSize进行压缩
     * 4、对Density>240的设备进行Bitmap的适配（压缩Density）
     * 5、2.3版本inNativeAlloc的使用
     * 6、4.4以下版本inPurgeable、inInputShareable的使用
     * 7、Bitmap的回收
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("InlinedApi")
    private static BitmapFactory.Options getBitmapOptions(Context context) {
        checkParam(context);

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = true;  //支持缩放
        option.inPreferredConfig = DEFAUL_BITMAP_CONFIG; //解码格式
        option.inPurgeable = true;            //当存储Pixel的内存空间在系统内存不足时是否可以被回收
        option.inPremultiplied = true;       //inPurgeable为true情况下才生效，是否可以共享一个InputStream
        option.inJustDecodeBounds = false;

        //当前版本小于2.3，包括 2.3时启用native的inNativeAlloc
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            Field field = null;
            try {
                field = BitmapFactory.Options.class.getDeclaredField("inNativeAlloc");
                field.setAccessible(true);
                field.setBoolean(option, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //当前屏幕的像素密度
        int displayDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
        float displayDensity = context.getResources().getDisplayMetrics().density;//1dp=多少px

        //当像素密度大于240时，设置像素密度和最终的像素密度为当前屏幕的0.75
        if (displayDensityDpi > DEFAULT_DENSITY && displayDensity > 1.5f) {
            int density = (int) (displayDensityDpi * SCALE_FACTOR);
            option.inDensity = density;
            option.inTargetDensity = density;
        }
        return option;
    }

    /**
     * 从inputStream中解析bitmap
     *
     * @param context
     * @param is      输入流
     * @return
     */
    public static Bitmap decodeBitamp(Context context, InputStream is) {
        checkParam(context);
        checkParam(is);
        return BitmapFactory.decodeStream(is, null, getBitmapOptions(context));
    }

    /**
     * 从一个文件的文件中解析bitmap
     *
     * @param context
     * @param pathName 存放文件的全路径
     * @return
     */
    public static Bitmap decodeBitmap(Context context, String pathName) {
        checkParam(context);
        checkParam(pathName);

        return BitmapFactory.decodeFile(pathName, getBitmapOptions(context));
    }

    /**
     * 从一个文件中解析bitmap 并输出为制定大小
     *
     * @param context
     * @param pathName 文件路径
     * @param width    目标宽
     * @param height   目标高
     * @return
     */
    public static Bitmap compressBitmap(Context context, String pathName, int width, int height) {
        checkParam(context);
        checkParam(pathName);

        InputStream is = null;
        try {
            is = new FileInputStream(pathName);
            return compressBitmap(context, is, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从输入流中解析bitmap 并输出为制定大小
     *
     * @param context
     * @param is
     * @param maxWidth  要输出的宽
     * @param maxHeight 要输出的高
     * @return
     */
    public static Bitmap compressBitmap(Context context, InputStream is, int maxWidth, int maxHeight) {
        checkParam(context);
        checkParam(is);

        //在解析之前设置 只获得bitmap的宽高等属性
        //实际就是先得到一个空的bitmap 这个空的bitmap包含了bitmap的宽高
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, option);
        //得到bitmap的宽和高
        int width = option.outWidth;
        int height = option.outHeight;
        //计算压缩比例
        int sampleSize = getRatioSize(width, height, maxWidth, maxHeight);
        BitmapFactory.Options opt = getBitmapOptions(context);
        //设置压缩比例
        opt.inSampleSize = sampleSize;
        return decodeBitamp(context, is);
    }

    /**
     * 判断参数是否为空
     *
     * @param param
     * @param <T>
     */
    private static <T> void checkParam(T param) {
        if (param == null) {
            throw new NullPointerException();
        }
    }

    /**
     * 快速进行图片的高斯模糊处理，先进行图片的缩放，再处理模糊，不然有可能会OOM
     *
     * @param bitmap
     * @return
     */
    public static Bitmap fastBlur(Bitmap bitmap) {
        float radius = 2;
        float scaleFactor = 6;  //值越大，缩放后的图片越小，模糊的效果越明显
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap scaleBitmp = Bitmap.createScaledBitmap(bitmap, (int) (width / scaleFactor), (int) (height / scaleFactor), false);
        Bitmap blurBitmp = doBlur(scaleBitmp, (int) radius, true);
        return blurBitmp;
    }

    /**
     * 图片的高斯模糊，毛玻璃处理
     *
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
