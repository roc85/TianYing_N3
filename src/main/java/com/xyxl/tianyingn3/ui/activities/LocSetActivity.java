package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.global.SettingSharedPreference;

public class LocSetActivity extends BaseActivity {

    private TextView textTitle;
    private ImageView imageBack;
    private RelativeLayout locTitleBox;
    private RadioButton switchmsgMsg;
    private TextView textmsgMsgOn;
    private RelativeLayout msgHomeMsgBox;
    private RadioButton switchmsgDev;
    private TextView textmsgDevOn;
    private RelativeLayout msgHomeDevBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_loc_set);
        initView();
    }

    private void initView() {
        textTitle = (TextView) findViewById(R.id.textTitle);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        locTitleBox = (RelativeLayout) findViewById(R.id.locTitleBox);
        switchmsgMsg = (RadioButton) findViewById(R.id.switchmsgMsg);
        textmsgMsgOn = (TextView) findViewById(R.id.textmsgMsgOn);
        msgHomeMsgBox = (RelativeLayout) findViewById(R.id.msgHomeMsgBox);
        switchmsgDev = (RadioButton) findViewById(R.id.switchmsgDev);
        textmsgDevOn = (TextView) findViewById(R.id.textmsgDevOn);
        msgHomeDevBox = (RelativeLayout) findViewById(R.id.msgHomeDevBox);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(SettingSharedPreference.getDataInt(this,LOCATION_TYPE_FLAG) == 0)
        {
            switchmsgMsg.setChecked(true);
            switchmsgDev.setChecked(false);
        }
        else if(SettingSharedPreference.getDataInt(this,LOCATION_TYPE_FLAG) == 1)
        {
            switchmsgMsg.setChecked(false);
            switchmsgDev.setChecked(true);
        }

        switchmsgMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    switchmsgMsg.setChecked(true);
                    switchmsgDev.setChecked(false);
                    SettingSharedPreference.setDataInt(LocSetActivity.this,LOCATION_TYPE_FLAG,0);
                }
            }
        });
        switchmsgDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    switchmsgMsg.setChecked(false);
                    switchmsgDev.setChecked(true);
                    SettingSharedPreference.setDataInt(LocSetActivity.this,LOCATION_TYPE_FLAG,1);
                }
            }
        });
    }
}
