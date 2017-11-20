package com.xyxl.tianyingn3.database;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/13 17:22
 * Version : V1.0
 * Introductions : 报文消息数据库
 */

public class Message_DB extends SugarRecord implements Serializable {
//    @Column(name = "msg_ID", unique = true, notNull = true)
//    @Expose
//    private long msgId; //消息id号

    @Expose
    //消息发送方地址
    private String sendAddress;

    @Expose
    //消息接收方地址
    private String rcvAddress;

    @Expose
    //消息类型 0-发送 1-接收
    private int msgType;

    @Expose
    //消息发送状态 0-发送中 1-发送成功 2-发送失败
    private int msgSendStatue;

    @Expose
    //消息发送方姓名
    private String sendUserName;

    @Expose
    //消息接收方姓名
    private String rcvUserName;

    @Expose
    //消息文字内容
    private String msgCon;

    @Expose
    //消息附带位置
    private String msgPos;

    @Expose
    //消息发送时间
    private long msgTime;

    @Expose
    //消息删除标识 0-有效 1-删除
    private int delFlag;

    @Expose
    //消息附加说明
    private String remark;

//    public long getMsgId() {
//        return msgId;
//    }
//
//    public void setMsgId(long msgId) {
//        this.msgId = msgId;
//    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getRcvAddress() {
        return rcvAddress;
    }

    public void setRcvAddress(String rcvAddress) {
        this.rcvAddress = rcvAddress;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgSendStatue() {
        return msgSendStatue;
    }

    public void setMsgSendStatue(int msgSendStatue) {
        this.msgSendStatue = msgSendStatue;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getRcvUserName() {
        return rcvUserName;
    }

    public void setRcvUserName(String rcvUserName) {
        this.rcvUserName = rcvUserName;
    }

    public String getMsgCon() {
        return msgCon;
    }

    public void setMsgCon(String msgCon) {
        this.msgCon = msgCon;
    }

    public String getMsgPos() {
        return msgPos;
    }

    public void setMsgPos(String msgPos) {
        this.msgPos = msgPos;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
