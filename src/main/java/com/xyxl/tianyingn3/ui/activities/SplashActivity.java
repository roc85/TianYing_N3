package com.xyxl.tianyingn3.ui.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Msg_DB;
import com.xyxl.tianyingn3.logs.LogUtil;

import java.security.Key;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 15:30
 * Version : V1.0
 * Introductions : 引导页面，软件启动页面
 */
public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mHandler.sendEmptyMessageDelayed(0,2000);

    }

    //创建一个handler，内部完成处理消息方法
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case 0:
                    //
                    OpenActivity(true, MainActivity.class);
                    break;

                default:
                    break;
            }
        }
    };
}
