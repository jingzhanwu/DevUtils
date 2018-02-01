package com.dev.jzw.helper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * @anthor created by 景占午
 * @date 2017/11/10 0010
 * @change
 * @describe 数据库操作管理类，主要负责对数据库的增 删 改 查等基本功能的封装
 **/
public class DBManager {
    private String TAG = "jzw-db";
    private Context mContext;
    private SQLiteOpenHelper mSQLiteOpenHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private Cursor mCursor;
    private StringBuffer mSqlBuffer;
    private boolean isSqlSuccessed = false;

    public DBManager(Context context) {
        super();
        this.mContext = context;
        mSqlBuffer = new StringBuffer();
    }

    public void create(String tableName, String sql) {
        if (isSqlSuccessed && isLegalSql(sql)) {
            if (mSQLiteOpenHelper == null) {
                mSQLiteOpenHelper = new SQLiteHelp(mContext, tableName, sql);
            }
        } else {
            Log.d(TAG, "sql 语句不合法");
        }
    }

    /**
     * 是否为合法Sql语句
     */
    private boolean isLegalSql(String sql) {
        if (sql != null && sql.length() > 1) {
            if ("(".equals(sql.charAt(0) + "") && ")".equals(sql.charAt(sql.length() - 1) + "")) {
                return true;
            }
        }
        return false;
    }

    public DBManager addPrimaryKey() {
        mSqlBuffer.append("_id integer primary key autoincrement,");
        return this;
    }

    public DBManager addText(String key) {
        mSqlBuffer.append(key + " Text,");
        return this;
    }

    public DBManager addBlob(String key) {
        mSqlBuffer.append(key + " blob,");
        return this;
    }

    public DBManager addInteger(String key) {
        mSqlBuffer.append(key + " integer,");
        return this;
    }


    /**
     * 获取SQL语句
     */
    public String getSql() {
        String sql = null;
        if (mSqlBuffer.length() > 0) {
            sql = mSqlBuffer.toString();
            sql = sql.substring(0, sql.length() - 1);
            sql = "(" + sql + ")";
            Log.i(TAG, "getSql: " + sql);
            mSqlBuffer = new StringBuffer();
            isSqlSuccessed = true;
        }
        return sql;
    }

    public void execSql(String sql) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.execSQL(sql);
        closeAll();
    }

    /**
     * 增加数据
     *
     * @param tableName      表名
     * @param nullColumnHack 非空字段名
     * @param values         数据源
     */
    public long insert(String tableName, String nullColumnHack, ContentValues values) {
        long num = 0;
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        num = mSQLiteDatabase.insert(tableName, nullColumnHack, values);
        closeAll();
        return num;
    }

    public long insert(String tableName, ContentValues values) {
        return insert(tableName, null, values);
    }

    /**
     * 更新
     *
     * @param values
     * @param whereArgs
     */
    public int update(String tabName, ContentValues values, String where, String[] whereArgs) {
        int num = 0;
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        num = mSQLiteDatabase.update(tabName, values, where, whereArgs);
        closeAll();
        return num;
    }

    /**
     * 批量插入数据
     *
     * @param tableName
     * @param datas
     */
    public long insertList(String tableName, List<ContentValues> datas) {
        long num = 0;
        if (datas != null && datas.size() > 0) {
            mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
            for (ContentValues value : datas) {
                num += mSQLiteDatabase.insert(tableName, null, value);
            }
            closeAll();
        }
        return num;
    }

    /**
     * 删除数据
     *
     * @param tableName   表名
     * @param whereClause （eg:"_id=?"）
     * @param whereArgs   （eg:new String[] { "01" } ）
     */
    public void delete(String tableName, String whereClause, String[] whereArgs) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
        closeAll();
    }

    public void remove(String tableName, String whereClause, String[] whereArgs) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
    }

    /**
     * 更新
     *
     * @param tableName   表名
     * @param values      更新的数据
     * @param whereClause 更新的条件（eg:_id = 01）
     */
    public void update(String tableName, ContentValues values, String whereClause) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.update(tableName, values, whereClause, null);
        closeAll();
    }

    /**
     * 查询
     *
     * @param tableName
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return mCursor 游标
     */
    public Cursor query(String tableName, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mCursor = mSQLiteDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
        return mCursor;
    }

    /**
     * 查询全部(查询后需要在调用的类中手动调用closeAll()方法来关闭全部函数)
     *
     * @param tableName 表名
     * @param orderBy   排序方式（asc升序，desc降序）
     * @return mCursor 游标
     */
    public Cursor queryAll(String tableName, String orderBy) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mCursor = mSQLiteDatabase.query(tableName, null, null, null, null, null, orderBy);
        return mCursor;
    }

    public Cursor queryAll(String tableName) {
        return queryAll(tableName, null);
    }

    /**
     * 从数据库中删除表
     *
     * @param tableName 表名
     */
    public void dropTable(String tableName) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.execSQL("drop table if exists " + tableName);
        Log.e(TAG, "已删除" + tableName + "表");
        closeAll();
    }

    /**
     * 删除表中的全部数据
     *
     * @param tableName 表名
     */
    public void deleteTableData(String tableName) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mSQLiteDatabase.execSQL("delete from " + tableName);
        Log.e(TAG, "已清空" + tableName + "表");
        closeAll();
    }

    /**
     * 判断某张表是否存在
     *
     * @param tableName 表名
     * @return true 存在
     */
    public boolean isTableExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            mSQLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
            mCursor = mSQLiteDatabase.rawQuery(sql, null);
            if (mCursor.moveToNext()) {
                int count = mCursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, tableName + "表不存在");
        }
        closeAll();
        return result;
    }

    /**
     * 获取表中有多少条数据
     *
     * @param tableName
     * @return
     */
    public int getDataNum(String tableName) {
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mCursor = mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
        int num = mCursor.getCount();
        closeAll();
        return num;
    }

    /**
     * 数据库是否存在要查询的这条数据
     *
     * @param tableName  表名
     * @param columnName 需要查询字段
     * @param data       需要查询数据
     * @return
     */
    public boolean hasThisData(String tableName, String columnName, String data) {
        boolean result = false;
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
        mCursor = mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
        while (mCursor.moveToNext()) {
            String columnValues = mCursor.getString(mCursor.getColumnIndex(columnName));
            // 有这条数据
            if (data.equals(columnValues)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void closeAll() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        } else {
            Log.e(TAG, "closeAll: mCursor已关闭");
        }
        if (mSQLiteOpenHelper != null) {
            mSQLiteOpenHelper.close();
        } else {
            Log.e(TAG, "closeAll: mSQLiteOpenHelper已关闭");
        }
        if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase.close();
        } else {
            Log.e(TAG, "closeAll: mSQLiteDatabase已关闭");
        }
    }
}
