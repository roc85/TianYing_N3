package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;

public class TestActivity extends AppCompatActivity {

    private TextView textSet;
    private TextView textTitle;
    private RelativeLayout setTitle;
    private ImageView imageSetHead;
    private ImageView imageQRCode;
    private TextView textUserName;
    private TextView textUserInfos;
    private LinearLayout loginedBox;
    private TextView textLogin;
    private RelativeLayout lrLine;
    private TextView textRegister;
    private LinearLayout logAndRegBox;
    private RelativeLayout userLoginBox;
    private ImageView imageBtArrow;
    private ImageView imageBtSet;
    private TextView textBtSetting;
    private RelativeLayout btSetting;
    private ImageView imageNoticeArrow;
    private ImageView imageNoticeSet;
    private TextView textNoticeSetting;
    private RelativeLayout noticeSetting;
    private ImageView imageMapArrow;
    private ImageView imageMapSet;
    private TextView textMapSetting;
    private RelativeLayout mapSetting;
    private ImageView imageLocArrow;
    private ImageView imageLocSet;
    private TextView textLocSetting;
    private RelativeLayout locSetting;
    private ImageView imageDatasArrow;
    private ImageView imageDatasSet;
    private TextView textDatasSetting;
    private RelativeLayout datasSetting;
    private ImageView imageMsgArrow;
    private ImageView imageMsgSet;
    private TextView textMsgSetting;
    private RelativeLayout msgSetting;
    private LinearLayout settingBox;
    private TextView textSetName;
    private RelativeLayout userBox;
    private ImageView imagebtSet;
    private TextView textBtSet;
    private RelativeLayout btSetBox;
    private ImageView imagePosSet;
    private TextView textPosSet;
    private RelativeLayout posSetBox;
    private LinearLayout setLineBox;
    private Chronometer chronometer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_set);
        initView();
    }

    private void initView() {
        textSet = (TextView) findViewById(R.id.textSet);
        textTitle = (TextView) findViewById(R.id.textTitle);
        setTitle = (RelativeLayout) findViewById(R.id.setTitle);
        imageSetHead = (ImageView) findViewById(R.id.imageSetHead);
        imageQRCode = (ImageView) findViewById(R.id.imageQRCode);
        textUserName = (TextView) findViewById(R.id.textUserName);
        textUserInfos = (TextView) findViewById(R.id.textUserInfos);
        loginedBox = (LinearLayout) findViewById(R.id.loginedBox);
        textLogin = (TextView) findViewById(R.id.textLogin);
        lrLine = (RelativeLayout) findViewById(R.id.lrLine);
        textRegister = (TextView) findViewById(R.id.textRegister);
        logAndRegBox = (LinearLayout) findViewById(R.id.logAndRegBox);
        userLoginBox = (RelativeLayout) findViewById(R.id.userLoginBox);
        imageBtArrow = (ImageView) findViewById(R.id.imageBtArrow);
        imageBtSet = (ImageView) findViewById(R.id.imageBtSet);
        textBtSetting = (TextView) findViewById(R.id.textBtSetting);
        btSetting = (RelativeLayout) findViewById(R.id.btSetting);
        imageNoticeArrow = (ImageView) findViewById(R.id.imageNoticeArrow);
        imageNoticeSet = (ImageView) findViewById(R.id.imageNoticeSet);
        textNoticeSetting = (TextView) findViewById(R.id.textNoticeSetting);
        noticeSetting = (RelativeLayout) findViewById(R.id.noticeSetting);
        imageMapArrow = (ImageView) findViewById(R.id.imageMapArrow);
        imageMapSet = (ImageView) findViewById(R.id.imageMapSet);
        textMapSetting = (TextView) findViewById(R.id.textMapSetting);
        mapSetting = (RelativeLayout) findViewById(R.id.mapSetting);
        imageLocArrow = (ImageView) findViewById(R.id.imageLocArrow);
        imageLocSet = (ImageView) findViewById(R.id.imageLocSet);
        textLocSetting = (TextView) findViewById(R.id.textLocSetting);
        locSetting = (RelativeLayout) findViewById(R.id.locSetting);
        imageDatasArrow = (ImageView) findViewById(R.id.imageDatasArrow);
        imageDatasSet = (ImageView) findViewById(R.id.imageDatasSet);
        textDatasSetting = (TextView) findViewById(R.id.textDatasSetting);
        datasSetting = (RelativeLayout) findViewById(R.id.datasSetting);
        imageMsgArrow = (ImageView) findViewById(R.id.imageMsgArrow);
        imageMsgSet = (ImageView) findViewById(R.id.imageMsgSet);
        textMsgSetting = (TextView) findViewById(R.id.textMsgSetting);
        msgSetting = (RelativeLayout) findViewById(R.id.msgSetting);
        settingBox = (LinearLayout) findViewById(R.id.settingBox);
        textSetName = (TextView) findViewById(R.id.textSetName);
        userBox = (RelativeLayout) findViewById(R.id.userBox);
        imagebtSet = (ImageView) findViewById(R.id.imagebtSet);
        textBtSet = (TextView) findViewById(R.id.textBtSet);
        btSetBox = (RelativeLayout) findViewById(R.id.btSetBox);
        imagePosSet = (ImageView) findViewById(R.id.imagePosSet);
        textPosSet = (TextView) findViewById(R.id.textPosSet);
        posSetBox = (RelativeLayout) findViewById(R.id.posSetBox);
        setLineBox = (LinearLayout) findViewById(R.id.setLineBox);
        chronometer2 = (Chronometer) findViewById(R.id.chronometer2);
    }
}
