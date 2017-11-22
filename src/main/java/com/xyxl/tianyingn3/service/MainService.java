package com.xyxl.tianyingn3.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BufferHandle;

import java.io.IOException;

/**
 * Created by rocgoo on 2017/11/9 下午1:45.
 * Function：主后台服务
 */

public class MainService extends Service{

    private FunctionThread functionThread;
    private long startAppTime;

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
//        mNotificationManager.cancelAll();
        LogUtil.e("onDestroy");
        App.finishAllActivity();
    }

    private void initService()
    {
        //初始化服务方法

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
