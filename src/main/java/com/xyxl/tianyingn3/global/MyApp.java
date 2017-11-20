package com.xyxl.tianyingn3.global;

import com.baidu.mapapi.SDKInitializer;
import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by Administrator on 2017/11/20 9:15
 * Version : V1.0
 * Introductions :
 */

public class MyApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();


        //初始化Sugar数据库
        SugarContext.init(this);

        //初始化百度地图
        SDKInitializer.initialize(this);

    }

    public void onTerminate()
    {
        super.onTerminate();
        SugarContext.terminate();
    }
}
