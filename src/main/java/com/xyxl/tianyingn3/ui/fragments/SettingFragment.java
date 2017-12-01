package com.xyxl.tianyingn3.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bean.MessageBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.bluetooth.BTDeviceInfos;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.ui.activities.BluetoothActivity;
import com.xyxl.tianyingn3.ui.activities.LocSetActivity;
import com.xyxl.tianyingn3.ui.activities.LocalDataSetActivity;
import com.xyxl.tianyingn3.ui.activities.LoginActivity;
import com.xyxl.tianyingn3.ui.activities.MapSetActivity;
import com.xyxl.tianyingn3.ui.activities.MsgSetActivity;
import com.xyxl.tianyingn3.ui.activities.MySetActivity;
import com.xyxl.tianyingn3.ui.activities.NoticeSetActivity;
import com.xyxl.tianyingn3.ui.activities.RegisterActivity;
import com.xyxl.tianyingn3.ui.activities.SavedMsgSetActivity;
import com.xyxl.tianyingn3.util.ImageUtil;

import java.io.File;

/**
 * Created by Administrator on 2017/11/13 16:58
 * Version : V1.0
 * Introductions : 设置功能页面
 */

public class SettingFragment extends BaseFragment {

    private boolean isTesting = false;

    //控件
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
    private RelativeLayout msgSavedSetting;
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
    private Chronometer timer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        textSet = (TextView) view.findViewById(R.id.textSet);
        textTitle = (TextView) view.findViewById(R.id.textTitle);
        setTitle = (RelativeLayout) view.findViewById(R.id.setTitle);
        imageSetHead = (ImageView) view.findViewById(R.id.imageSetHead);
        imageQRCode = (ImageView) view.findViewById(R.id.imageQRCode);
        textUserName = (TextView) view.findViewById(R.id.textUserName);
        textUserInfos = (TextView) view.findViewById(R.id.textUserInfos);
        loginedBox = (LinearLayout) view.findViewById(R.id.loginedBox);
        textLogin = (TextView) view.findViewById(R.id.textLogin);
        lrLine = (RelativeLayout) view.findViewById(R.id.lrLine);
        textRegister = (TextView) view.findViewById(R.id.textRegister);
        logAndRegBox = (LinearLayout) view.findViewById(R.id.logAndRegBox);
        userLoginBox = (RelativeLayout) view.findViewById(R.id.userLoginBox);
        imageBtArrow = (ImageView) view.findViewById(R.id.imageBtArrow);
        imageBtSet = (ImageView) view.findViewById(R.id.imageBtSet);
        textBtSetting = (TextView) view.findViewById(R.id.textBtSetting);
        btSetting = (RelativeLayout) view.findViewById(R.id.btSetting);
        imageNoticeArrow = (ImageView) view.findViewById(R.id.imageNoticeArrow);
        imageNoticeSet = (ImageView) view.findViewById(R.id.imageNoticeSet);
        textNoticeSetting = (TextView) view.findViewById(R.id.textNoticeSetting);
        noticeSetting = (RelativeLayout) view.findViewById(R.id.noticeSetting);
        imageMapArrow = (ImageView) view.findViewById(R.id.imageMapArrow);
        imageMapSet = (ImageView) view.findViewById(R.id.imageMapSet);
        textMapSetting = (TextView) view.findViewById(R.id.textMapSetting);
        mapSetting = (RelativeLayout) view.findViewById(R.id.mapSetting);
        imageLocArrow = (ImageView) view.findViewById(R.id.imageLocArrow);
        imageLocSet = (ImageView) view.findViewById(R.id.imageLocSet);
        textLocSetting = (TextView) view.findViewById(R.id.textLocSetting);
        locSetting = (RelativeLayout) view.findViewById(R.id.locSetting);
        imageDatasArrow = (ImageView) view.findViewById(R.id.imageDatasArrow);
        imageDatasSet = (ImageView) view.findViewById(R.id.imageDatasSet);
        textDatasSetting = (TextView) view.findViewById(R.id.textDatasSetting);
        datasSetting = (RelativeLayout) view.findViewById(R.id.datasSetting);
        imageMsgArrow = (ImageView) view.findViewById(R.id.imageMsgArrow);
        imageMsgSet = (ImageView) view.findViewById(R.id.imageMsgSet);
        textMsgSetting = (TextView) view.findViewById(R.id.textMsgSetting);
        msgSavedSetting = (RelativeLayout) view.findViewById(R.id.msgSavedSetting);
        msgSetting = (RelativeLayout) view.findViewById(R.id.msgSetting);
        settingBox = (LinearLayout) view.findViewById(R.id.settingBox);
        textSetName = (TextView) view.findViewById(R.id.textSetName);
        userBox = (RelativeLayout) view.findViewById(R.id.userBox);
        imagebtSet = (ImageView) view.findViewById(R.id.imagebtSet);
        textBtSet = (TextView) view.findViewById(R.id.textBtSet);
        btSetBox = (RelativeLayout) view.findViewById(R.id.btSetBox);
        imagePosSet = (ImageView) view.findViewById(R.id.imagePosSet);
        textPosSet = (TextView) view.findViewById(R.id.textPosSet);
        posSetBox = (RelativeLayout) view.findViewById(R.id.posSetBox);
        setLineBox = (LinearLayout) view.findViewById(R.id.setLineBox);
        timer = (Chronometer) view.findViewById(R.id.chronometer2);

        if(BtConnectInfo.getInstance().isConnect())
        {
            if(TextUtils.isEmpty(BtConnectInfo.getInstance().getUserName()))
            {
                loginedBox.setVisibility(View.VISIBLE);
                imageQRCode.setVisibility(View.VISIBLE);
                logAndRegBox.setVisibility(View.GONE);
                textUserName.setText(BtConnectInfo.getInstance().getUserName());
                textUserInfos.setText(BtConnectInfo.getInstance().getUserInfo());
                Contact_DB me = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaAddress(BdCardBean.getInstance().getIdNum()));
                if(me != null)
                {
                    Picasso.with(getHoldingActivity()).load(new File(me.getHead())).
                            transform(transformation).
                            error(R.mipmap.ic_launcher).
                            placeholder(R.mipmap.ic_launcher_round).
                            into(imageSetHead);
                }

            }
            else
            {
                loginedBox.setVisibility(View.GONE);
                imageQRCode.setVisibility(View.GONE);
                logAndRegBox.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            loginedBox.setVisibility(View.GONE);
            imageQRCode.setVisibility(View.GONE);
            logAndRegBox.setVisibility(View.VISIBLE);
        }

        initListener();
    }

    private void initListener() {
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,LoginActivity.class);
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,RegisterActivity.class);
            }
        });

        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,BluetoothActivity.class);
            }
        });

        noticeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,NoticeSetActivity.class);
            }
        });

        msgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,MsgSetActivity.class);
            }
        });

        msgSavedSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,SavedMsgSetActivity.class);
            }
        });

        locSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,LocSetActivity.class);
            }
        });

        mapSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,MapSetActivity.class);
            }
        });

        datasSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,LocalDataSetActivity.class);
            }
        });

        textSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false,MySetActivity.class);
            }
        });
    }


    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    if(isTesting)
                    {
                        AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(BdCardBean.getInstance().getIdNum(), 1, 2, TestMsg.TEST_MSG), null));
                        Toast.makeText(getActivity(),"发送中",Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(1,62*1000);
                    }
                    break;
                case 2: // Notify change

                    break;
            }
        }
    };

    /**
     * 定义订阅者
     */
    @Subscribe
    //蓝牙设备连接信息
    public void setContent(MessageBean data) {
        LogUtil.i(TestMsg.getInstance().toString());
        Toast.makeText(getActivity(), "MSG"+data.getSendAddress()+"："+data.getMsgCon(), Toast.LENGTH_SHORT).show();
//        textResult.setText(TestMsg.getInstance().toString());
    }

    @Subscribe
    public void setContent(BdCardBean data) {

//        textInfos.setText(BdCardBean.getInstance().toString()+"\nPos:"+ MyPosition.getInstance().getMyLon()+" "+MyPosition.getInstance().getMyLat());
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册到bus事件总线中
        AppBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }

    Transformation transformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int width = source.getWidth();
            int height = source.getHeight();
            int size = Math.min(width, height);
            Bitmap blankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blankBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);
            if (source != null && !source.isRecycled()) {
                source.recycle();
            }
            return blankBitmap;
        }

        @Override
        public String key() {
            return "squareup";
        }
    };
}



//        timer = (Chronometer)view.view.findViewById(R.id.chronometer2);
//        timer.start();
//        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//
//            }
//        });

//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendICA(0, 0),null));
//                if(btnTest.getText().equals("发送报文"))
//                {
//                    btnTest.setText("测试中");
//                    TestMsg.getInstance().ClearDatas();
//                    textResult.setText(TestMsg.getInstance().toString());
//                    isTesting = true;
//                    mHandler.sendEmptyMessage(1);
////                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA("306631", 1, 2, TestMsg.TEST_MSG), null));
//                }
//                else
//                {
//                    btnTest.setText("发送报文");
//                    isTesting = false;
//                }
//            }
//        });
//
//        btnBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
//            }
//        });


//        btSetBox = (RelativeLayout)view.view.findViewById(R.id.btSetBox);
//        btSetBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
//            }
//        });
//
//        setHead = (ImageView)view.view.findViewById(R.id.imageSetHead);
//        setHead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getHoldingActivity().OpenActivity(false, LoginActivity.class);
//            }
//        });