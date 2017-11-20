package com.xyxl.tianyingn3.global;

/**
 * Created by Administrator on 2017/11/15 16:07
 * Version : V1.0
 * Introductions :
 */

public class TestMsg {

    public final static String TEST_MSG = "星宇芯联北斗消息测试abc123，。、！@#";

    private int sendNum;
    private int sendOkNum;
    private int rcvNum;

    private static TestMsg instance;

    public static TestMsg getInstance()
    {
        if(instance == null)
        {
            instance = new TestMsg();
        }
        return instance;
    }

    public void ClearDatas()
    {
        sendNum = 0;
        sendOkNum = 0;
        rcvNum = 0;
    }

    public void AddSendNum()
    {
        sendNum++;
    }

    public void AddSendOkNum()
    {
        sendOkNum++;
    }

    public void AddRcvNum()
    {
        rcvNum++;
    }

    public String toString()
    {
        return "发送"+sendNum+" 已发送"+sendOkNum+" 收到"+rcvNum;
    }
}
