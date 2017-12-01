package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.SettingSharedPreference;

public class NoticeSetActivity extends BaseActivity {

    private TextView textTitle;
    private ImageView imageBack;
    private RelativeLayout noticeTitleBox;
    private Switch switchNoticeBar;
    private TextView textNoticeOn;
    private RelativeLayout noticeBarBox;
    private Switch switchNoticeMsg;
    private TextView textNoticeMsgOn;
    private RelativeLayout noticeHomeMsgBox;
    private Switch switchNoticeDev;
    private TextView textNoticeDevOn;
    private RelativeLayout noticeHomeDevBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_notice_set);
        initView();
    }

    private void initView() {
        textTitle = (TextView) findViewById(R.id.textTitle);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        noticeTitleBox = (RelativeLayout) findViewById(R.id.noticeTitleBox);
        switchNoticeBar = (Switch) findViewById(R.id.switchNoticeBar);
        textNoticeOn = (TextView) findViewById(R.id.textNoticeOn);
        noticeBarBox = (RelativeLayout) findViewById(R.id.noticeBarBox);
        switchNoticeMsg = (Switch) findViewById(R.id.switchNoticeMsg);
        textNoticeMsgOn = (TextView) findViewById(R.id.textNoticeMsgOn);
        noticeHomeMsgBox = (RelativeLayout) findViewById(R.id.noticeHomeMsgBox);
        switchNoticeDev = (Switch) findViewById(R.id.switchNoticeDev);
        textNoticeDevOn = (TextView) findViewById(R.id.textNoticeDevOn);
        noticeHomeDevBox = (RelativeLayout) findViewById(R.id.noticeHomeDevBox);

        if(SettingSharedPreference.getDataInt(this,NOTICE_BAR_FLAG) == 0)
        {
            switchNoticeBar.setChecked(true);
        }
        else if(SettingSharedPreference.getDataInt(this,NOTICE_BAR_FLAG) == 01)
        {
            switchNoticeBar.setChecked(false);
        }

        switchNoticeBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSharedPreference.setDataInt(NoticeSetActivity.this,NOTICE_BAR_FLAG,isChecked?0:1);

            }
        });

        if(SettingSharedPreference.getDataInt(this,NOTICE_HOME_MSG_FLAG) == 0)
        {
            switchNoticeMsg.setChecked(true);
        }
        else if(SettingSharedPreference.getDataInt(this,NOTICE_HOME_MSG_FLAG) == 01)
        {
            switchNoticeMsg.setChecked(false);
        }

        switchNoticeMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSharedPreference.setDataInt(NoticeSetActivity.this,NOTICE_HOME_MSG_FLAG,isChecked?0:1);

            }
        });

        if(SettingSharedPreference.getDataInt(this,NOTICE_HOME_DEVICE_FLAG) == 0)
        {
            switchNoticeDev.setChecked(true);
        }
        else if(SettingSharedPreference.getDataInt(this,NOTICE_HOME_DEVICE_FLAG) == 01)
        {
            switchNoticeDev.setChecked(false);
        }

        switchNoticeDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSharedPreference.setDataInt(NoticeSetActivity.this,NOTICE_HOME_DEVICE_FLAG,isChecked?0:1);

            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
