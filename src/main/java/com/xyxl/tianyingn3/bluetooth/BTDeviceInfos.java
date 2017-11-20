package com.xyxl.tianyingn3.bluetooth;

/**
 * Created by Administrator on 2017/11/14 16:05
 * Version : V1.0
 * Introductions : 用于描述蓝牙设备
 */

public class BTDeviceInfos {
    private String btName;
    private String btMac;
    private int btType;

    public BTDeviceInfos(String name, String mac, int t)
    {
        this.btName = name;
        this.btMac = mac;
        this.btType = t;
    }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getBtMac() {
        return btMac;
    }

    public void setBtMac(String btMac) {
        this.btMac = btMac;
    }

    public int getBtType() {
        return btType;
    }

    public void setBtType(int btType) {
        this.btType = btType;
    }
}
