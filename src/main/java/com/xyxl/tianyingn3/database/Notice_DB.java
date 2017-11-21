package com.xyxl.tianyingn3.database;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/21 9:55
 * Version : V1.0
 * Introductions : 通知数据库
 */

public class Notice_DB extends SugarRecord implements Serializable {

    @Expose
    //通知所属用户
    private String noticeAddress;

    @Expose
    //通知所属号码
    private String noticeNum;

    @Expose
    //通知类型 0-新消息 1-软件使用
    private int noticeType;

    @Expose
    //通知内容
    private String noticeCon;

    @Expose
    //通知时间
    private long noticeTime;

    @Expose
    //通知备注
    private String noticeRemark;

    public long getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(long noticeTime) {
        this.noticeTime = noticeTime;
    }

    public String getNoticeAddress() {
        return noticeAddress;
    }

    public void setNoticeAddress(String noticeAddress) {
        this.noticeAddress = noticeAddress;
    }

    public String getNoticeNum() {
        return noticeNum;
    }

    public void setNoticeNum(String noticeNum) {
        this.noticeNum = noticeNum;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeCon() {
        return noticeCon;
    }

    public void setNoticeCon(String noticeCon) {
        this.noticeCon = noticeCon;
    }

    public String getNoticeRemark() {
        return noticeRemark;
    }

    public void setNoticeRemark(String noticeRemark) {
        this.noticeRemark = noticeRemark;
    }
}
