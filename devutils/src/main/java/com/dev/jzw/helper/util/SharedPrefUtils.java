package com.dev.jzw.helper.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by 景占午 on 4/19 0019.
 * 存储一些全局使用的静态数据，支持boolean，string，int，map，
 * 实体对象等
 */
public class SharedPrefUtils {

    private final static String PREF_NAME = "dev";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        if (sharedPref == null) {
            sharedPref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        }
        if (editor == null) {
            editor = sharedPref.edit();
        }
    }

    public static <T> String saveObj(String key, T value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
        return key;
    }

    public static String getObj(String key) {
        return sharedPref.getString(key, "null");
    }

    public static int getIntObj(String key) {
        return sharedPref.getInt(key, 0);
    }

    public static long getLongObj(String key) {
        return sharedPref.getLong(key, 0);
    }

    public static boolean getBooleanObj(String key) {
        return sharedPref.getBoolean(key, false);
    }


    /**
     * 保存一个实体对象
     *
     * @param key
     * @param t
     * @param <T>
     */
    public static <T> void saveEntry(String key, T t) {
        Gson gson = buildGson();
        String entry = gson.toJson(t);
        editor.putString(key, entry).commit();
    }

    /**
     * 取出对应的实体对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getEntry(String key, Class<T> clazz) {
        String entry = sharedPref.getString(key, "");
        if (entry != null) {
            Gson gson = buildGson();
            T t = gson.fromJson(entry, clazz);
            return t;
        } else {
            return null;
        }
    }

    /**
     * 讲一个map保存成string
     *
     * @param key
     * @param map
     * @param <T>
     */
    public static <T> void saveMap(String key, Map<Object, T> map) {
        Gson gson = buildGson();
        String json = gson.toJson(map);
        editor.putString(key, json);
        editor.commit();
    }

    /**
     * 根据key取出对应的map
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> Map<Object, T> getMap(String key) {
        String entry = sharedPref.getString(key, "");
        if (entry != null) {
            Gson gson = buildGson();
            Type token = new TypeToken<Map<Object, T>>() {
            }.getType();
            Map<Object, T> map = gson.fromJson(entry, token);
            return map;
        } else {
            return null;
        }
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 删除一条数据
     **/
    public static void delete(String key) {
        editor.remove(key);
        editor.commit();
    }

    public static Gson buildGson() {
        return new GsonBuilder().setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                .serializeNulls() //智能null,支持输出值为null的属性
                .setPrettyPrinting()//格式化输出（序列化）
                .create();
    }
}
