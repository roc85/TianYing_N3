package com.xyxl.tianyingn3.bean;

/**
 * Created by Administrator on 2017/11/22 15:00
 * Version : V1.0
 * Introductions :
 */

public class SearchResultsBean {

    //结果类型 0-联系人 1-报文
    private int type;
    private String name;
    private String con;
    private long dbId;

    public SearchResultsBean(int t, String n, String c, long i)
    {
        this.type = t;
        this.name = n;
        this.con = c;
        this.dbId = i;
    }

    @Override
    public String toString() {
        return name+" "+con;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }
}
