package com.xyxl.tianyingn3.bean;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : 消息实体类
 */

public class MessageBean {
    //消息id
    private long msgId;
    //消息发送方地址
    private String sendAddress;
    //消息接收方地址
    private String rcvAddress;
    //消息类型
    private int msgType;
    //消息发送状态
    private int msgSendStatue;
    //消息发送方姓名
    private String sendUserName;
    //消息接收方姓名
    private String rcvUserName;
    //消息文字内容
    private String msgCon;
    //消息附带位置
    private String msgPos;
    //消息发送时间
    private long msgTime;
    //消息删除标识
    private int delFlag;
    //消息附加说明
    private String remark;

    public MessageBean()
    {

    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

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
