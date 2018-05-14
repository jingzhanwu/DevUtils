package com.dev.jzw.helper.selector;

import android.support.annotation.Keep;

import java.util.List;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/5/4 0004
 * @change
 * @describe 多及列表选择器 实体接口
 **/
@Keep
public interface ISelectorEntry<T extends ISelectorEntry> {

    /**
     * 获取item显示名称
     *
     * @return
     */
    String getSelectorName();

    /**
     * 获取item的id
     *
     * @return
     */
    String getSelectorId();

    /**
     * 获取下一级列表
     *
     * @return
     */
    List<T> getSelectorChildreen();

}
