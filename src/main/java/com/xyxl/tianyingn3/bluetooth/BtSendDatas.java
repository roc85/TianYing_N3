package com.xyxl.tianyingn3.bluetooth;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/11/15 11:13
 * Version : V1.0
 * Introductions : 需要通过蓝牙发送的数据
 */

public class BtSendDatas {
    //数据类型 0-字符串 1-字节 2-均有效
    private int type;
    private String strData;
    private byte[] byteDatas;

    public BtSendDatas()
    {

    }

    public BtSendDatas(int t, String s, byte[] b)
    {
        type = t;
        if(!TextUtils.isEmpty(s))
        {
            strData = s;
        }

        if(b != null && b.length>0)
        {
            byteDatas = b;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public byte[] getByteDatas() {
        return byteDatas;
    }

    public void setByteDatas(byte[] byteDatas) {
        this.byteDatas = byteDatas;
    }
}
