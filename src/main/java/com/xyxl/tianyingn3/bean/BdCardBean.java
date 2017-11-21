package com.xyxl.tianyingn3.bean;

import com.xyxl.tianyingn3.global.FinalDatas;

/**
 * Created by Administrator on 2017/11/13 14:01
 * Version : V1.0
 * Introductions : 北斗卡信息类
 */

public class BdCardBean {

    //北斗卡号
    private String idNum;
    //服务频度
    private int serviceFrequency;
    //服务等级
    private int cardLv;
    //有效字节长度
    private int enableBytesLen;
    //波束状态
    private int[] beamLvs;
    //当前发送消息id
    private long msgSendingId = -1;
    //备注说明
    private String remark;

    private static BdCardBean instance;

    public static BdCardBean getInstance()
    {
        if (instance == null)
        {
            instance = new BdCardBean();
        }
        return  instance;
    }

    private static final int CARD_NUM_MAX = 7;//北斗卡号位数
    public static String FormatCardNum(String num)
    {
        while(num.length()<CARD_NUM_MAX)
        {
            num = "0" + num;
        }
        return num;
    }

//    public void setBdCard(String num, int serFreq, int cLv, int bLen, String rem)
//    {
//        idNum = num;
//        serviceFrequency = serFreq;
//        cardLv = cLv;
//        enableBytesLen = bLen;
//        remark = rem;
//    }
//
//    public void setBdCard(String num, int serFreq, int cLv, int bLen)
//    {
//        setBdCard(num,serFreq,cLv,bLen,null);
//    }
//
//    public void setBdCard(String num, int serFreq, int cLv)
//    {
//        setBdCard(num,serFreq,cLv, FinalDatas.BD_BYTES_LENS[cLv],null);
//    }

    @Override
    public String toString() {
        String beams = "";
        for(int i = 0;i < beamLvs.length; i++)
        {
            beams+=(beamLvs[i]+"");
        }
        return "BD Card:"+idNum+" serviceFrequency:"+serviceFrequency+" CardLv:"+cardLv+"\nBeams:"+beams;
    }

    public long getMsgSendingId() {
        return msgSendingId;
    }

    public void setMsgSendingId(long msgSendingId) {
        this.msgSendingId = msgSendingId;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public int getServiceFrequency() {
        return serviceFrequency;
    }

    public void setServiceFrequency(int serviceFrequency) {
        this.serviceFrequency = serviceFrequency;
    }

    public int getCardLv() {
        return cardLv;
    }

    public void setCardLv(int cardLv) {
        this.cardLv = cardLv;
    }

    public int getEnableBytesLen() {
        return enableBytesLen;
    }

    public void setEnableBytesLen(int enableBytesLen) {
        this.enableBytesLen = enableBytesLen;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int[] getBeamLvs() {
        return beamLvs;
    }

    public void setBeamLvs(int[] beamLvs) {
        this.beamLvs = beamLvs;
    }
}
