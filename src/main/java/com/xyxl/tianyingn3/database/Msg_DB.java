package com.xyxl.tianyingn3.database;

import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/17 14:36
 * Version : V1.0
 * Introductions :
 */

public class Msg_DB extends SugarRecord implements Serializable {
    @Column(name = "msg_ID", unique = true, notNull = true)
    private long msgId; //消息id号

    //消息发送方地址
    private String address;

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
