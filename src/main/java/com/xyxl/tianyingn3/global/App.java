package com.xyxl.tianyingn3.global;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;


//import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BufferHandle;
import com.xyxl.tianyingn3.util.CommonUtil;
import com.xyxl.tianyingn3.util.DeviceUtil;
import com.xyxl.tianyingn3.util.ImageUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rocgoo on 2017/11/8.
 * Application类
 * 用于做一些全局处理
 * 管理activity
 */

public class App extends Application{


    protected static Context context;
    protected static String appName;

    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivitys = Collections
            .synchronizedList(new LinkedList<Activity>());

    @Override
    public void onCreate() {
        super.onCreate();
//        context = this.getApplicationContext();
//        appName = getAppNameFromSub();
//        registerActivityListener();

        //初始化Sugar数据库
        SugarContext.init(this);

        //初始化百度地图
        SDKInitializer.initialize(this);

        //创建app缓存目录
        DeviceUtil.createAPPFolder(getResources().getString(R.string.app_name_en),this);
        ImageUtil.createImageFileInCameraFolder(getResources().getString(R.string.app_name_en),this);

        //初始化数据缓冲区分析方法BufferHandle
        BufferHandle.getInstance().setContext(getApplicationContext());
//
        //代码崩溃记录
        ExceptionHandler mCustomCrashHandler = ExceptionHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(this);

        //友盟
//        MobclickAgent.setDebugMode(true);
//        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);

        //Logger初始化
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .methodCount(4)
                .tag(getResources().getString(R.string.app_tag))   //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        //日志文件保存
//        formatStrategy = CsvFormatStrategy.newBuilder()
//                .tag(getResources().getString(R.string.app_tag))
//
//                .build();
//
//        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));

    }


    public static String getAppName() {
        return appName;
    }

    public static Context getContext() {
        return context;
    }

    protected String  getAppNameFromSub()
    {
        return "TianYingN3";
    }


    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
//        LogUtils.d("activityList:size:"+mActivitys.size());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
//        LogUtils.d("activityList:size:"+mActivitys.size());
    }



    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return null;
        }
        Activity activity = mActivitys.get(mActivitys.size()-1);
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size()-1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 按照指定类名找到activity
     *
     * @param cls
     * @return
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity;

    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    /**
     * 退出应用程序
     */
    public  static void appExit() {
        try {
//            LogUtils.e("app exit");
            finishAllActivity();
        } catch (Exception e) {
        }
    }


    private void registerActivityListener() {
        //android版本在4.0之后才可以使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
//                    LogUtil.i(activity.getLocalClassName()+" is pushed");
                    pushActivity(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {

//                    LogUtil.i(activity.getLocalClassName()+" START");
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null==mActivitys&&mActivitys.isEmpty()){
                        return;
                    }
                    if (mActivitys.contains(activity)){
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
//                        LogUtil.i(activity.getLocalClassName()+" is poped");
                        popActivity(activity);
                    }
                }
            });
        }
    }

}

