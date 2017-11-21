package com.dev.jzw.helper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor created by 景占午
 * @date 2017/11/10 0010
 * @change
 * @describe 数据库操作的顶层封装，包括常用的增，删，改，查等功能全部
 * 包含在内，用时不用关心数据库的关闭和打开，全部由这个类统一处理，我们
 * 只需要继承这个类实现对应的抽象方法即可
 **/
public abstract class BaseDao<T> {
    private String TAG = "KnifeInfoDao";
    /**
     * 默认的表名称，如果在tableName这个抽象方法中部设置则
     * 使用这个默认值
     */
    private static String mTableName = "ds_knifeinfo";
    /**
     * 保存这个表的所有字段名，但不包括 _id 这个字段，这个字段在
     * 创建这个表时会自动加进去，默认为该表的唯一主键，自增长
     */
    private List<String> mAllColum = new ArrayList<>();
    /**
     * 负责数据库的管理，打开，关闭数据库，管理SQLiteOpenHelp对象等
     * 对面向数据库的最直接的操作进行一层封装
     */
    private DBManager mDBManager;

    /**
     * 由子类实现，设置数据库表的名称
     * 可以不设置，则默认使用默认的表名
     *
     * @return 数据库表名
     */
    public abstract String tableName();

    /**
     * 设置类型为文本（TEXT）类型的字段名称，
     * 子类实现时将要创建的表的所有文本类型的字段名称用一个
     * 数组保存并返回即可
     *
     * @return 字段名称数组
     */
    public abstract String[] textColum();

    /**
     * 设置类型为整形（Integer）类型的字段名称，
     * 子类实现时将要创建的表的所有文本类型的字段名称用一个
     * 数组保存并返回即可
     *
     * @return 字段名称数组
     */
    public abstract String[] integerColum();

    /**
     * 设置类型为二进制（BLOB）类型的字段名称，
     * 子类实现时将要创建的表的所有文本类型的字段名称用一个
     * 数组保存并返回即可
     *
     * @return 字段名称数组
     */
    public abstract String[] blobColum();

    /**
     * 定义的表中作为唯一标识的字段名称（不是指 _id 字段），这个字段可能在更新数据，删除，查询，判断记录是否存在等
     * 操作中使用。比如：一个订单记录的订单编号，这个编号在真个订单列表中便是唯一的，而且是不
     * 重复的。
     *
     * @return id 字段名称
     */
    public abstract String idColumName();

    /**
     * id 字段所对应的具体的某一条记录的值，这个方法获取的值肯定是 idColumName() 这个
     * 抽象方法所定义的字段的值。在判断记录是否存在，更新，删除等操作时动态获取这个值。
     *
     * @param t 具体的一个操作对象，在操作的地方动态传入，实际上是子类传入的一个具体实体类
     * @return 实体类对应的 id 字段的值
     */
    public abstract String getIdColumValue(T t);

    /**
     * 子类必须实现这个抽象方法，这个方法在所有的的插入，查询等操作中都会调用，
     * 它负责将一个具体的实体类对象转换成对应的一个ContentVulues对象，而数据库
     * 在插入是就是使用的这个ContentValues对象
     *
     * @param t 具体的实体类对象
     * @return 转化后的contentValues对象
     */
    public abstract ContentValues createContentValues(T t);

    /**
     * 有查询操作时，这个抽象方法也一定要实现，他负责将一个Cursor对象中的数据
     * 取出并转化成对应的实体类对象
     *
     * @param cursor 数据库查询结果cursor对象
     * @return 具体的实体类对象
     */
    public abstract T getObjectFromCursor(Cursor cursor);


    /**
     * 唯一的带参构造参数。需要一个上下文对象，构造方法中主要做以下几件事情
     * 1：获取子类实现的数据库名称
     * 2：初始化DBManager对象
     * 3：获取子类设置的各个类型的字段数组
     * 4：根据字段名称 拼接sql并创建一个表
     *
     * @param context
     */
    public BaseDao(Context context) {
        if (!TextUtils.isEmpty(tableName())) {
            mTableName = tableName();
        }
        mDBManager = new DBManager(context);
        createTable();
    }

    /**
     * 主要负责添加表字段，创建数据库表
     */
    private void createTable() {
        mDBManager.addPrimaryKey();
        if (textColum() != null && textColum().length > 0) {
            for (String colum : textColum()) {
                mDBManager.addText(colum);
                mAllColum.add(colum);
            }
        }
        if (integerColum() != null && integerColum().length > 0) {
            for (String colum : integerColum()) {
                mDBManager.addInteger(colum);
                mAllColum.add(colum);
            }
        }
        if (blobColum() != null && blobColum().length > 0) {
            for (String colum : blobColum()) {
                mDBManager.addBlob(colum);
                mAllColum.add(colum);
            }
        }

        if (mAllColum.size() <= 1) {
            new NullPointerException("not find any table colum!");
        } else {
            String sql = mDBManager.getSql();
            mDBManager.create(mTableName, sql);
        }
    }

    /**
     * 获取定义的id 字段名称
     *
     * @return
     */
    public String getIDColumName() {
        return idColumName();
    }

    /**
     * 新增一条记录，这里的新增已经过滤了重复添加的操作，在上层调用时
     * 不需要额外判断 记录是否存在。如果已经有记录就走更新的逻辑，如果
     * 没有就新增一条
     *
     * @param t
     * @return
     */
    public long addData(T t) {
        long num = 0;
        ContentValues values = createContentValues(t);
        String idValue = getIdColumValue(t);
        if (hasThisData(getIDColumName(), idValue)) {
            num = mDBManager.update(mTableName, values, getIDColumName() + "=?", new String[]{idValue});
        } else {
            num = mDBManager.insert(mTableName, values);
        }
        Log.i(TAG, "更新了" + num + "条数据 ");
        return num;
    }

    /**
     * 新增一批记录，
     *
     * @param datas
     * @return
     */
    public long addData(List<T> datas) {
        long num = 0;
        if (datas != null && datas.size() > 0) {
            List<ContentValues> values = new ArrayList<>();
            for (T info : datas) {
                ContentValues value = createContentValues(info);
                values.add(value);
            }
            num = mDBManager.insertList(mTableName, values);
            Log.i(TAG, mTableName + ":增加了" + num + "条数据 ");
        }
        return num;
    }

    /**
     * 删除一条数据，删除完毕后关闭数据库
     */
    public void delData(String id) {
        if (hasThisData(getIDColumName(), id)) {
            mDBManager.delete(mTableName, getIDColumName() + "=?", new String[]{id});
        }
        Log.i(TAG, "delData:删除了一条数据 ");
    }

    /**
     * 删除一条记录，但是删除完毕后比关闭数据库,
     * 调用者可以自己手动调用删除的方法
     *
     * @param id
     */
    public void removeItem(String id) {
        mDBManager.remove(mTableName, getIDColumName() + "=?", new String[]{id});
        Log.i(TAG, "delData:删除了一条数据 ");
    }

    /**
     * 关闭数据库，包括cursor等一切资源
     */
    public void closeDatabase() {
        mDBManager.closeAll();
    }

    /**
     * 清空表中的内容
     */
    public void clearTable() {
        mDBManager.deleteTableData(mTableName);
    }

    /**
     * 是否是空表
     *
     * @return true 是空表
     */
    public boolean isEmptyTable() {
        if (mDBManager.getDataNum(mTableName) > 0) {
            return false;
        }
        return true;
    }

    /**
     * 数据库是否存在要查询的这条数据
     *
     * @param columnName 查询的字段
     * @param data       查询的数据
     * @return
     */
    public boolean hasThisData(String columnName, String data) {
        return mDBManager.hasThisData(mTableName, columnName, data);
    }

    /**
     * 修改一条数据的内容
     *
     * @param whereclause 条件
     */
    public void updateData(T info, String whereclause) {
        ContentValues values = createContentValues(info);
        mDBManager.update(mTableName, values, whereclause);
        Log.i(TAG, "modifyData: 修改了一条数据");
    }

    /**
     * 查询表中所有的数据，不排序
     *
     * @return
     */
    public List<T> findAllData() {
        return findAllData(null);
    }

    /**
     * 查询表中所有数据，并进行排序
     *
     * @param orderColumName 排序字段名
     * @return
     */
    public List<T> findAllData(String orderColumName) {
        List<T> list = new ArrayList<>();
        Cursor cursor = mDBManager.queryAll(mTableName, orderColumName);
        while (cursor.moveToNext()) {
            T obj = getObjectFromCursor(cursor);
            list.add(obj);
        }
        mDBManager.closeAll();
        return list;
    }
}
