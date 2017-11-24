package com.xyxl.tianyingn3.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);
//        String send = intent.getStringExtra("send");
//        GlobalData globalData = (GlobalData)context.getApplicationContext();
        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals("notification_clicked")) {
            //处理点击事件
            int noticeType = intent.getIntExtra("notice_type",-1);
            if (noticeType == 0)
            {
                //报文信息
                long _id = intent.getLongExtra("msg_id",-1);

                Message_DB msg = Message_DB.findById(Message_DB.class , _id);
                if(msg != null)
                {
                    Intent newintent = new Intent(context,ChatActivity.class);
                    newintent.putExtra("msg",msg);
                    newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//关键的一步，设置启动模式
                    context.startActivity(newintent);
                }
            }
        }
//
//        if (action.equals("notification_cancelled")) {
//            //处理滑动清除和点击删除事件
//        }
    }
}