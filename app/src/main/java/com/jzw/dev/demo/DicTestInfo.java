package com.jzw.dev.demo;


import com.dev.jzw.helper.selector.ISelectorEntry;

import java.util.List;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by Administrator
 * @date 2018/5/11 0011
 * @change
 * @describe describe
 **/
public class DicTestInfo implements ISelectorEntry<DicTestInfo> {

    private String id;
    private String value;
    private String type;
    private String orgName;
    private List<DicTestInfo> list;

    public DicTestInfo(String id, String value, String type, String orgName) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.orgName = orgName;
    }

    @Override
    public String getSelectorName() {
        return getValue();
    }

    @Override
    public String getSelectorId() {
        return getId();
    }

    @Override
    public List<DicTestInfo> getSelectorChildren() {
        return getList();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<DicTestInfo> getList() {
        return list;
    }

    public void setList(List<DicTestInfo> list) {
        this.list = list;
    }
}
