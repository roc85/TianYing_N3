package com.xyxl.tianyingn3.database;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/1 13:53
 * Version : V1.0
 * Introductions :
 */

public class NewMsgCount_DB extends SugarRecord implements Serializable {
    @Column(name = "num_ID", unique = true, notNull = true)
    @Expose
    private long numId;

    @Expose
    private long userId;

    @Expose
    private String bdNum;

    @Expose
    private int num;

    public long getNumId() {
        return numId;
    }

    public void setNumId(long numId) {
        this.numId = numId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getBdNum() {
        return bdNum;
    }

    public void setBdNum(String bdNum) {
        this.bdNum = bdNum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

/***********************************************************/

    public void NumClear()
    {
        this.num = 0;
    }

    public void NumPlusOne()
    {
        this.num ++;
    }
}
