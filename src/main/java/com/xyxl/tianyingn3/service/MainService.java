package com.xyxl.tianyingn3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by rocgoo on 2017/11/9 下午1:45.
 * Function：主后台服务
 */

public class MainService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
