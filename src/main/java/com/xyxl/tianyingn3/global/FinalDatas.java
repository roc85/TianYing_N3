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

    //北斗服务器中心号码
    public static final String BD_SERVICE_NUM = "bd_service_num";

    /****1-关闭 0-开启**********************************/
    //振动提醒开关
    public static final String VIBRATION_FLAG = "vibration_flag";
    //铃声提醒开关
    public static final String RING_FLAG = "ring_flag";
    //通知栏提醒开关
    public static final String NOTICE_BAR_FLAG = "notice_bar_flag";
    //首页新消息通知提醒开关
    public static final String NOTICE_HOME_MSG_FLAG = "notice_home_msg_flag";
    //首页设备通知提醒开关
    public static final String NOTICE_HOME_DEVICE_FLAG = "notice_home_device_flag";
    //定位方式参数 0-设备 1-手机
    public static final String LOCATION_TYPE_FLAG = "location_type_flag";

    /********************************************************************/
    //蓝牙默认连接失败最大次数
    public static final int BT_AUTO_CONNECT_MAX_TIME = 5;

    //快捷功能
    public static final String[] HOME_BTNS_INFOS = {"新建报文", "新建联系人", "设备管理", "提醒设置", "地图管理"};
    public static final String HOME_BTNS_FLAG = "homeBtnsFlag";
}
