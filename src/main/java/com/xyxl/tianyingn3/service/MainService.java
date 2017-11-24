package com.xyxl.tianyingn3.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BufferHandle;
import com.xyxl.tianyingn3.util.NotificationBroadcastReceiver;

import java.io.IOException;

/**
 * Created by rocgoo on 2017/11/9 下午1:45.
 * Function：主后台服务
 */

public class MainService extends Service{

    private FunctionThread functionThread;
    private long startAppTime;

    //通知栏相关
    private NotificationCompat.Builder mBuilder;
    private int     				   notifyId;
    public  NotificationManager 	   mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flag,int startId){
        super.onStartCommand(intent, flag, startId);
        LogUtil.e("onStartCommand");
        return startId;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        LogUtil.e("onCreate");
        initService();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 初始化通知栏
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        LogUtil.e("onDestroy");
        App.finishAllActivity();
    }

    private void initService()
    {
        //初始化服务方法

        //初始化通知栏
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
//				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量

                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    public void ShowNotice(String str , String bdId)
    {
        //通知栏显示
//        if(SettingSharedPreference.getNotic(mContext)==0)
//        {
//            try {
//                notifyId = Integer.valueOf(bdId);
//            } catch (Exception e) {
//                // TODO: handle exception
//                notifyId = 100;
//            }
//            mNotificationManager.cancel(notifyId);
//
////    		mBuilder.setContentTitle("消息提醒")
////    		.setContentText("收到来自"+str+"的新消息")
////    		.setTicker("有新的消息");//通知首次出现在通知栏，带上升动画效果的
////
////    		mNotificationManager.notify(notifyId, mBuilder.build());
//
//            //新的通知
//            Intent intentClick = new Intent(mContext, NotificationBroadcastReceiver.class);
//            intentClick.setAction("notification_clicked");
//            intentClick.putExtra("send", bdId);
//            intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 1);
//            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(mContext, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
//
//            Intent intentCancel = new Intent(mContext, NotificationBroadcastReceiver.class);
//            intentCancel.setAction("notification_cancelled");
//            intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 2);
//            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(mContext, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);
//
//            mBuilder.setContentTitle("消息提醒")
//                    .setContentText("您有来自"+str+"的新消息")
//                    .setTicker("有新的消息")
//                    .setContentIntent(pendingIntentClick)
//                    .setDeleteIntent(pendingIntentCancel);//通知首次出现在通知栏，带上升动画效果的
//
//            mNotificationManager.notify(notifyId, mBuilder.build());
//        }
    }

    /**
     * 用于完成各项工作的线程
     *
     * @author Roc
     * @version 2016-3-30
     */
    private class FunctionThread extends Thread {
        //用于控制线程开启关闭
        protected boolean isRun;

        @Override
        public void run() {
            super.run();

            //完成线程内功能
            while (isRun) {


            }
        }

    }
}
