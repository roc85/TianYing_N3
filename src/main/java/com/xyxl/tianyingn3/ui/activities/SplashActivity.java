package com.xyxl.tianyingn3.ui.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.Msg_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.util.DataUtil;

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
//        //去除【本机】联系人
//        List<Contact_DB> cList = Contact_DB.find(Contact_DB.class,"contact_Name = ?", getResources().getString(R.string.this_device));
//        if(cList.size()>0)
//        {
//            for(int i=0;i<cList.size();i++)
//            {
//                cList.get(i).delete();
//            }
//        }


        //写入上次北斗卡号
        BdCardBean.getInstance().setIdNum(SettingSharedPreference.getDataString(this,LAST_BD_CARD_NUM));

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

        //初始化预制消息
        String[] savedMsgs = {"你好","再见","请上报位置","集合","出发"};
        for(int i=0;i<SAVED_MSG_MAX;i++)
        {
            if(TextUtils.isEmpty(SettingSharedPreference.getDataString(this,SAVED_MSG_INFO+i)))
            {
                SettingSharedPreference.setDataString(this,SAVED_MSG_INFO+i,savedMsgs[i]);
            }
        }

        //初始化快捷按钮
        String homeBtns = SettingSharedPreference.getDataString(this,HOME_BTNS_FLAG);

        if(TextUtils.isEmpty(homeBtns))
        {
            homeBtns = "";
            for(int i=0;i<HOME_BTNS_INFOS.length;i++)
            {
                homeBtns += HOME_BTNS_INFOS[i]+"f"+1+"g";
            }
            SettingSharedPreference.setDataString(this,HOME_BTNS_FLAG,homeBtns);
        }

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
