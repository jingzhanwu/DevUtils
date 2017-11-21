package com.dev.jzw.helper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @anthor created by 景占午
 * @date 2017/11/10 0010
 * @change
 * @describe 数据库操作辅助类，负责数据库的创建，表的创建，数据库更新等
 **/
public class SQLiteHelp extends SQLiteOpenHelper {

    private String TAG = "SQLiteHelp";
    /**
     * 数据库名称，必须以 .db 结尾
     */
    private static String mDBName = "ds_knife.db";
    /**
     * 数据库版本号，最小的版本号为1，若设置为小于1的值则会出错
     */
    private static int mVersion = 1;

    private String mTableName = "";
    private String mSql;

    public SQLiteHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * @param context
     * @param name
     * @param sql
     */
    public SQLiteHelp(Context context, String name, String sql) {
        this(context, mDBName, null, mVersion);
        this.mTableName = name;
        this.mSql = sql;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db != null) {
            Log.i(TAG, "tabname>>>>" + mTableName);
            Log.i(TAG, "sql>>>>>" + mSql);
            db.execSQL("create table if not exists " + mTableName + mSql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "newVersion>" + newVersion);
        Log.i(TAG, "oldVersion>" + oldVersion);
        db.execSQL("drop table if exists" + mTableName);
        onCreate(db);
    }
}
