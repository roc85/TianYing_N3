package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.SettingSharedPreference;

public class MsgSetActivity extends BaseActivity {

    private TextView textTitle;
    private ImageView imageBack;
    private RelativeLayout msgTitleBox;
    private TextView textmsgMsgOn;
    private RelativeLayout msgHomeMsgBox;
    private TextView textmsgDevOn;
    private RelativeLayout msgHomeDevBox;
    //铃声开关
    private Switch switchmsgMsg;
    //振动开关
    private Switch switchmsgDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setExitFlag(0);
        setContentView(R.layout.activity_msg_set);
        initView();
    }

    private void initView() {
        textTitle = (TextView) findViewById(R.id.textTitle);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        msgTitleBox = (RelativeLayout) findViewById(R.id.msgTitleBox);

        switchmsgMsg = (Switch) findViewById(R.id.switchmsgMsg);
        textmsgMsgOn = (TextView) findViewById(R.id.textmsgMsgOn);
        msgHomeMsgBox = (RelativeLayout) findViewById(R.id.msgHomeMsgBox);
        switchmsgDev = (Switch) findViewById(R.id.switchmsgDev);
        textmsgDevOn = (TextView) findViewById(R.id.textmsgDevOn);
        msgHomeDevBox = (RelativeLayout) findViewById(R.id.msgHomeDevBox);


        if(SettingSharedPreference.getDataInt(this,RING_FLAG) == 0)
        {
            switchmsgMsg.setChecked(true);
        }
        else if(SettingSharedPreference.getDataInt(this,RING_FLAG) == 1)
        {
            switchmsgMsg.setChecked(false);
        }

        switchmsgMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSharedPreference.setDataInt(MsgSetActivity.this,RING_FLAG,isChecked?0:1);

            }
        });

        if(SettingSharedPreference.getDataInt(this,VIBRATION_FLAG) == 0)
        {
            switchmsgDev.setChecked(true);
        }
        else if(SettingSharedPreference.getDataInt(this,VIBRATION_FLAG) == 1)
        {
            switchmsgDev.setChecked(false);
        }

        switchmsgDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSharedPreference.setDataInt(MsgSetActivity.this,VIBRATION_FLAG,isChecked?0:1);

            }
        });
    }
}
