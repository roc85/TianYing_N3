package com.xyxl.tianyingn3.bean;

/**
 * Created by Administrator on 2017/11/16 14:31
 * Version : V1.0
 * Introductions : 当前蓝牙连接信息类
 */

public class BtConnectInfo {
    public static BtConnectInfo instance;
    public static BtConnectInfo getInstance()
    {
        if(instance == null)
        {
            instance = new BtConnectInfo();
        }
        return instance;
    }

    private String btName;
    private String btMac;
    private boolean isConnect;
    private String userName;
    private String userInfo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
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

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
}
