package com.xyxl.tianyingn3.bean;

import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.logs.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/11/13 15:19
 * Version : V1.0
 * Introductions : 软件配置类,单例
 */

public class SettingBean {

    //铃声提醒开关 0-是 1-否
    private int ringFlag;
    //振动提醒开关 0-是 1-否
    private int vibrationFlag;
    //服务器地址
    private String serviceIP;
    //服务器端口
    private String servicePort;
    //定位标识 0-设备 1-手机
    private int locFlag;
    //通知栏标识 0-是 1-否
    private int noticeFlag;
    //锁屏信息标识 0-是 1-否
    private int lockScreenFlag;
    //蓝牙设备名
    private String btDeviceName;
    //蓝牙设备地址
    private String btDeviceMac;
    //蓝牙是否自动连接 0-是 1-否
    private int btConnectFlag;
    //北斗中心号
    private String bdServiceNum;

    private static SettingBean instance;

    public static SettingBean getInstance() {
        if (instance == null) {
            instance = new SettingBean();
        }
        return instance;
    }

    public void SetBeanDatas(String jsonStr)
    {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            ringFlag = jsonObject.getInt("ringFlag");
            vibrationFlag = jsonObject.getInt("vibrationFlag");
            serviceIP = jsonObject.getString("serviceIP");
            servicePort = jsonObject.getString("servicePort");
            btDeviceName = jsonObject.getString("btDeviceName");
            btDeviceMac = jsonObject.getString("btDeviceMac");
            bdServiceNum = jsonObject.getString("bdServiceNum");
            locFlag = jsonObject.getInt("locFlag");
            noticeFlag = jsonObject.getInt("noticeFlag");
            lockScreenFlag = jsonObject.getInt("lockScreenFlag");
            btConnectFlag = jsonObject.getInt("btConnectFlag");
        } catch (JSONException e) {
            LogUtil.e(e.toString());
        }
    }

    public String toString()
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("ringFlag", ringFlag);
            jsonObject.put("vibrationFlag", vibrationFlag);
            jsonObject.put("serviceIP", serviceIP);
            jsonObject.put("servicePort", servicePort);
            jsonObject.put("locFlag", locFlag);
            jsonObject.put("noticeFlag", noticeFlag);
            jsonObject.put("lockScreenFlag", lockScreenFlag);
            jsonObject.put("btDeviceName", btDeviceName);
            jsonObject.put("btDeviceMac", btDeviceMac);
            jsonObject.put("btConnectFlag", btConnectFlag);
            jsonObject.put("bdServiceNum", bdServiceNum);
        } catch (JSONException e) {
            LogUtil.e(e.toString());

        }
        return jsonObject.toString();
    }

    public int getBtConnectFlag() {
        return btConnectFlag;
    }

    public void setBtConnectFlag(int btConnectFlag) {
        this.btConnectFlag = btConnectFlag;
    }

    public int getRingFlag() {
        return ringFlag;
    }

    public void setRingFlag(int ringFlag) {
        this.ringFlag = ringFlag;
    }

    public int getVibrationFlag() {
        return vibrationFlag;
    }

    public void setVibrationFlag(int vibrationFlag) {
        this.vibrationFlag = vibrationFlag;
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public int getLocFlag() {
        return locFlag;
    }

    public void setLocFlag(int locFlag) {
        this.locFlag = locFlag;
    }

    public int getNoticeFlag() {
        return noticeFlag;
    }

    public void setNoticeFlag(int noticeFlag) {
        this.noticeFlag = noticeFlag;
    }

    public int getLockScreenFlag() {
        return lockScreenFlag;
    }

    public void setLockScreenFlag(int lockScreenFlag) {
        this.lockScreenFlag = lockScreenFlag;
    }

    public String getBtDeviceName() {
        return btDeviceName;
    }

    public void setBtDeviceName(String btDeviceName) {
        this.btDeviceName = btDeviceName;
    }

    public String getBtDeviceMac() {
        return btDeviceMac;
    }

    public void setBtDeviceMac(String btDeviceMac) {
        this.btDeviceMac = btDeviceMac;
    }

    public String getBdServiceNum() {
        return bdServiceNum;
    }

    public void setBdServiceNum(String bdServiceNum) {
        this.bdServiceNum = bdServiceNum;
    }
}
