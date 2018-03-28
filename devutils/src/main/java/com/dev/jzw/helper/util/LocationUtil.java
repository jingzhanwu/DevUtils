package com.dev.jzw.helper.util;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/1/18 0018
 * @change
 * @describe 定位，位置服务相关工具类
 **/
public class LocationUtil {

    //地球平均半径
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static String getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2)
                                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        String ext = "m";
        if (s > 1000) {
            //转换成公里
            s = Math.round(s / 1000);
            ext = "km";
        }
        return s + ext;
    }

    //把经纬度转为度（°）
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
