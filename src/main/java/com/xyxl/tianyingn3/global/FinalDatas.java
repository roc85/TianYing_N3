package com.xyxl.tianyingn3.global;

/**
 * Created by rocgoo on 2017/11/9 下午1:47.
 * Function：
 */

public interface FinalDatas {

    //北斗卡等级对应有效字节数
    public static final int[] BD_BYTES_LENS = {0, 0, 0, 78, 117};

    /**********本地存储关键字******************************************/
    //蓝牙设备相关
    public static final String BT_DEVICE_NAME = "btDeviceName";
    //北斗卡等级对应有效字节数
    public static final String BT_DEVICE_MAC = "btDeviceMac";
    //上一次连接北斗卡号
    public static final String LAST_BD_CARD_NUM = "lastBdCardNum";

    //预制消息
    public static final String SAVED_MSG_INFO = "saveMsgInfo";
    //预制消息最大数目
    public static final int SAVED_MSG_MAX = 5;
    /********************************************************************/
    //蓝牙默认连接失败最大次数
    public static final int BT_AUTO_CONNECT_MAX_TIME = 5;
}
