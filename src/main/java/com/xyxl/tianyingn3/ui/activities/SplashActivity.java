package com.xyxl.tianyingn3.ui.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Msg_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
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

        initData();

        mHandler.sendEmptyMessageDelayed(0,1000);

    }

    private void initData() {
        //去除【本机】联系人
        List<Contact_DB> cList = Contact_DB.find(Contact_DB.class,"contact_Name = ?", getResources().getString(R.string.this_device));
        if(cList.size()>0)
        {
            for(int i=0;i<cList.size();i++)
            {
                cList.get(i).delete();
            }
        }

        //去除上次剩余通知
        Notice_DB.deleteAll(Notice_DB.class);

        Notice_DB n = new Notice_DB();
        n.setNoticeType(1);
        n.setNoticeTime(System.currentTimeMillis());
        n.setNoticeRemark("");
        n.setNoticeNum("");
        n.setNoticeAddress("");
        n.setNoticeCon("欢迎使用天应助手N3");
        n.save();
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
