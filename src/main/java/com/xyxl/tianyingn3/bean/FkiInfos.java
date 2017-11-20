package com.xyxl.tianyingn3.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16 13:32
 * Version : V1.0
 * Introductions : 正在发送中消息反馈响应类
 */

public class FkiInfos {
    public static FkiInfos instance;

    public static FkiInfos getInstance()
    {
        if(instance == null)
        {
            instance = new FkiInfos();
        }
        return instance;
    }

    private int sendingMsgId = -1;

    public int getSendingMsgId() {
        return sendingMsgId;
    }

    public void setSendingMsgId(int sendingMsgId) {
        this.sendingMsgId = sendingMsgId;
    }

    private List<Integer> msgSendIdList = new ArrayList<Integer>();

    public void AddMsgId(int id)
    {
        msgSendIdList.add(id);
    }

    public int getSendingCount()
    {
        return msgSendIdList.size();
    }

    public void RemoveMsgId(int id)
    {
        Integer tmp = id;
        msgSendIdList.remove(tmp);
    }
}
