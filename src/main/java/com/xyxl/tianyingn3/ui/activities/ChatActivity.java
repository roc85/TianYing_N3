package com.xyxl.tianyingn3.ui.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.bluetooth.BluetoothService;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.database.NewMsgCount_DB;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.customview.ChatAdapter;
import com.xyxl.tianyingn3.ui.customview.DialogView;
import com.xyxl.tianyingn3.util.CommonUtil;
import com.xyxl.tianyingn3.util.DataUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 具体消息页面
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener ,FinalDatas{

    private Message_DB thisMsg;
    private ImageView imageInfo;
    private ImageView imageBack;
    private ImageView imageHead;
    private TextView textUserName;
    private TextView textAddInfo;
    private LinearLayout userInfos;
    private RelativeLayout titleBox;
    private ListView msgLists;

    //输入部分控件
    private ImageView imageDeviceSend;
    private ImageView imagePhoneSend;
    private ImageView imageAddInputSend;
    private EditText editConMsg;
    private RelativeLayout inputConBox;
    private TextView textPosInfos;
    private Switch posSwitch;
    private RelativeLayout addBox;
    private Chronometer timer;

    //变量
    private String userName, userBdNum;
    private Contact_DB thisContact;
    private List<Message_DB> chatDatas;
    private ChatAdapter chatAdapter;
    private int textmax;
    //发送模式 0-设备 1-手机 （-1）-无效
    private int sendMode = 0;
    //附带位置标识
    private int posSend = 0;

    private List<String> msgSavedList = new ArrayList<String>();

    private int choosePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setExitFlag(0);

        initData();
        initView();

    }

    private void initData() {
        try {
            thisMsg = (Message_DB) getIntent().getExtras().get("msg");
            String num = "";
            if(thisMsg.getMsgType() == 0)
            {
                num = thisMsg.getRcvAddress();
                thisContact = Contact_DB.findById(Contact_DB.class,thisMsg.getRcvUserId());
            }
            else if(thisMsg.getMsgType() == 1)
            {
                num = thisMsg.getSendAddress();
                thisContact = Contact_DB.findById(Contact_DB.class,thisMsg.getSendUserId());
            }

            userBdNum = num;
            if( DataUtil.isPhoneNum(num))
            {
//                thisContact = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaPhone(userBdNum));
                sendMode = 1;
            }
            else if( DataUtil.isBdNum(num))
            {
//                thisContact = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaAddress(userBdNum));
                sendMode = 0;
            }
            else
            {
                sendMode = -1;
            }


            if(thisContact != null && !DataUtil.isPhoneNum(thisContact.getPhone()) && !DataUtil.isBdNum(thisContact.getBdNum()))
            {
                //此联系人已无卡号
                sendMode = -1;

            }

            chatDatas = Message_DB.find(Message_DB.class,
                    "del_Flag = ? AND " +
                            "(( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?))",
                    new String[]{"0",BdCardBean.getInstance().getIdNum(),num,"0",num,BdCardBean.getInstance().getIdNum() ,"1"},
                    null,"msg_Time",null);

            chatAdapter = new ChatAdapter(ChatActivity.this,chatDatas);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(CommonUtil.Str2int(userBdNum));






        } catch (Exception e) {
            LogUtil.e(e.toString());
            finish();
        }

        msgSavedList.clear();
        for(int i = 0;i<SAVED_MSG_MAX;i++)
        {
            if(!TextUtils.isEmpty(SettingSharedPreference.getDataString(this,SAVED_MSG_INFO+i)))
            {
                msgSavedList.add(SettingSharedPreference.getDataString(this,SAVED_MSG_INFO+i));
            }
        }

    }

    private void initView() {
        imageInfo = (ImageView) findViewById(R.id.imageInfo);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageHead = (ImageView) findViewById(R.id.imageHead);
        textUserName = (TextView) findViewById(R.id.textUserName);
        textAddInfo = (TextView) findViewById(R.id.textAddInfo);
        userInfos = (LinearLayout) findViewById(R.id.userInfos);
        titleBox = (RelativeLayout) findViewById(R.id.titleBox);
        msgLists = (ListView) findViewById(R.id.msgLists);

        imageDeviceSend = (ImageView) findViewById(R.id.imageDeviceSend);
        imagePhoneSend = (ImageView) findViewById(R.id.imagePhoneSend);
        imageAddInputSend = (ImageView) findViewById(R.id.imageAddInputSend);
        editConMsg = (EditText) findViewById(R.id.editConMsg);
        inputConBox = (RelativeLayout) findViewById(R.id.inputConBox);
        textPosInfos = (TextView) findViewById(R.id.textPosInfo);
        posSwitch=(Switch)findViewById(R.id.switchPosSend);
        addBox = (RelativeLayout) findViewById(R.id.addSendBox);
        timer=(Chronometer)findViewById(R.id.chatTimer);
        timer.start();

        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //刷新位置信息
                byte[] lon = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon());
                byte[] lat = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat());

                textPosInfos.setText(GpsData.GetPosDisplayStr(lon)+","+GpsData.GetPosDisplayStr(lat));

                //刷新字数信息
                try {
                    if(sendMode == 0)
                    {
                        textmax = (BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]-(posSwitch.isChecked()?11:0));
                        if(textmax<0)
                        {
                            textmax =0;
                        }

                        textAddInfo.setText
                                ((editConMsg.getText().toString().getBytes("GB2312").length)+"/"
                                        +textmax);
                    }
                    else if(sendMode == 1)
                    {
                        textmax = (BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]-11);
                        if(textmax<0)
                        {
                            textmax =0;
                        }
                        textAddInfo.setText
                                ((editConMsg.getText().toString().getBytes("GB2312").length)+"/"
                                        +textmax);
                    }

                    if((editConMsg.getText().toString().getBytes("GB2312").length)>textmax)
                    {
                        textAddInfo.setTextColor(Color.RED);
                    }
                    else
                    {
                        textAddInfo.setTextColor(Color.BLACK);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        msgLists.setAdapter(chatAdapter);
        scrollMyListViewToBottom();

        imageBack.setOnClickListener(this);
        textUserName.setOnClickListener(this);

        textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
        textAddInfo.setTextColor(getResources().getColor(R.color.black));

        initUI();

        //设置用户
        if(thisMsg.getMsgType() == 0)
        {
            userName = thisMsg.getRcvUserName();
        }
        else if(thisMsg.getMsgType() == 1)
        {
            userName = thisMsg.getSendUserName();
        }
        if(thisContact == null)
        {
            textUserName.setText(userBdNum);
        }
        else
        {
            textUserName.setText(userName);
        }

//        if(thisContact != null)
//        {
//            Picasso.with(this).load(new File(thisContact.getHead())).
//                    transform(transformation).
//                    placeholder(R.mipmap.ic_launcher_round).
//                    into(imageHead);
//        }

        //设置发送模式按钮
        if(thisContact == null || TextUtils.isEmpty(thisContact.getBdNum()) || TextUtils.isEmpty(thisContact.getPhone()))
        {
//            imageSendType.setVisibility(View.GONE);
        }

        if(sendMode == 0)
        {
            imageDeviceSend.setSelected(true);
            imagePhoneSend.setSelected(false);
        }
        else if(sendMode == 1)
        {
            imageDeviceSend.setSelected(false);
            imagePhoneSend.setSelected(true);
        }
        else if(sendMode == -1)
        {
            imageDeviceSend.setSelected(false);
            imagePhoneSend.setSelected(false);
        }

        //位置选择
        posSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                try {
                    textAddInfo.setText
                            ((editConMsg.getText().toString().getBytes("GB2312").length+(posSwitch.isChecked()?10:0))+"/"
                                    +BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        //根据输入框输入值的改变来过滤搜索
        editConMsg.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if(!TextUtils.isEmpty(s.toString()))
                {
                    editConMsg.setTextColor(getResources().getColor(R.color.black));

                    try {
                        textAddInfo.setText((editConMsg.getText().toString().getBytes("GB2312").length+(posSwitch.isChecked()?10:0))+"/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(s.toString().getBytes().length>BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()])
                        {
                            editConMsg.setTextColor(getResources().getColor(R.color.red));

                            textAddInfo.setTextColor(getResources().getColor(R.color.red));
                        }
                        else
                        {
                            editConMsg.setTextColor(getResources().getColor(R.color.black));

                            textAddInfo.setTextColor(getResources().getColor(R.color.black));
                        }
//                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //软键盘发送
        editConMsg.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {
					 /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }

                    SendMsg();
                }

                return false;
            }
        });

        //输入框长按
        editConMsg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogView.ShowChooseDialog(ChatActivity.this,msgSavedList,mHandler);
                return false;
            }
        });


        //具体输入部分代码
        imageDeviceSend.setSelected(true);
        imageDeviceSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sendMode == -1) {
                    ShowToast("此联系人无北斗卡号，无法选择");
                    imageDeviceSend.setSelected(false);
                } else if (thisContact != null && !TextUtils.isEmpty(thisContact.getBdNum())) {
                    imageDeviceSend.setSelected(true);
                    imagePhoneSend.setSelected(false);
                    sendMode = 0;
                    userBdNum = thisContact.getBdNum();
                    editConMsg.setHint(getResources().getString(R.string.send_to_device));
                } else if (thisContact == null && DataUtil.isBdNum(userBdNum)) {
                    imageDeviceSend.setSelected(true);
                    imagePhoneSend.setSelected(false);
                    sendMode = 0;
                    editConMsg.setHint(getResources().getString(R.string.send_to_device));
                } else {
                    ShowToast("无北斗卡号，无法选择");
                    imageDeviceSend.setSelected(false);
                }

            }
        });

        imagePhoneSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendMode == -1) {
                    ShowToast("此联系人无手机号，无法选择");
                    imagePhoneSend.setSelected(false);
                } else if (thisContact != null && !TextUtils.isEmpty(thisContact.getPhone())) {
                    imageDeviceSend.setSelected(false);
                    imagePhoneSend.setSelected(true);
                    sendMode = 1;
                    editConMsg.setHint(getResources().getString(R.string.send_to_phone));
                    posSwitch.setChecked(false);
                    posSend = 0;
                    userBdNum = thisContact.getBdNum();
                } else if (thisContact == null && DataUtil.isPhoneNum(userBdNum)) {
                    imageDeviceSend.setSelected(false);
                    imagePhoneSend.setSelected(true);
                    sendMode = 1;
                    editConMsg.setHint(getResources().getString(R.string.send_to_phone));
                    posSwitch.setChecked(false);
                    posSend = 0;
                } else {
                    ShowToast("无手机号，无法选择");
                    imagePhoneSend.setSelected(false);
                }

            }
        });

        imageAddInputSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h = addBox.getLayoutParams().height;
                if(h == 0)
                {
                    RelativeLayout.LayoutParams relaParams =
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMyDpHeight(100f));
                    relaParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    addBox.setLayoutParams(relaParams);
//                    byte[] lon = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon());
//                    byte[] lat = GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat());
//
//                    textPosInfos.setText(GpsData.GetPosDisplayStr(lon)+","+GpsData.GetPosDisplayStr(lat));

                }
                else
                {
                    RelativeLayout.LayoutParams relaParams =
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMyDpHeight(0));
                    relaParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    addBox.setLayoutParams(relaParams);

                }
            }
        });

        editConMsg.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {
					 /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }

                    if(BtConnectInfo.getInstance().isConnect())
                    {
                        SendMsg();
                    }
                    else
                    {
                        ShowToast(getResources().getString(R.string.cant_send_msg));
                    }
                }

                return false;
            }
        });

        posSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    posSend = 1;
                }
                else
                {
                    posSend = 0;
                }
            }
        });

        msgLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                choosePosition = position;
                ShowDelDialog();
                return true;
            }
        });
    }

    public void ShowDelDialog() {
        //*******************************//

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        View view = View.inflate(ChatActivity.this, R.layout.dialog_confirm, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.textTitle);
        tvTitle.setText("是否删除该消息");
        final Button btnOk = (Button) view.findViewById(R.id.buttonOk);
        final Button btnEsc = (Button) view.findViewById(R.id.buttonEsc);

        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message_DB msg = chatDatas.get(choosePosition);
                if(msg != null)
                {
                    msg.setDelFlag(1);
                    msg.save();
                }

                RefreshUI();

                dialog.dismiss();
            }
        });
        btnEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMyDpHeight(0));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addBox.setLayoutParams(relaParams);
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
    private void RefreshUI()
    {

//        chatDatas = Message_DB.find(Message_DB.class,"send_Address = ? AND rcv_Address = ?",
//                new String[]{BdCardBean.getInstance().getIdNum(),thisMsg.getRcvAddress()},null,"msg_Time",null);

//        chatDatas = Message_DB.find(Message_DB.class,
//                "( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?)",
//                new String[]{BdCardBean.getInstance().getIdNum(),userBdNum,"0",userBdNum,BdCardBean.getInstance().getIdNum() ,"1"},
//                null,"msg_Time",null);
        chatDatas = Message_DB.find(Message_DB.class,
                "del_Flag = ? AND " +
                        "(( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?))",
                new String[]{"0",BdCardBean.getInstance().getIdNum(),userBdNum,"0",userBdNum,BdCardBean.getInstance().getIdNum() ,"1"},
                null,"msg_Time",null);
        chatAdapter = new ChatAdapter(ChatActivity.this,chatDatas);
        chatAdapter.notifyDataSetChanged();
        msgLists.setAdapter(chatAdapter);
        scrollMyListViewToBottom();
//        msgLists.scrollTo(msgLists.getScrollX(),msgLists.getHeight());

        //更新联系人
        thisContact = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaAddress(userBdNum));
        textUserName.setText(userName);

        if(thisContact != null)
        {
            Picasso.with(this).load(new File(thisContact.getHead())).
                    transform(transformation).
                    placeholder(R.mipmap.ic_launcher_round).
                    into(imageHead);
        }

        //设置发送模式按钮
        if(thisContact == null || TextUtils.isEmpty(thisContact.getBdNum()) || TextUtils.isEmpty(thisContact.getPhone()))
        {
//            imageSendType.setVisibility(View.GONE);
        }

        //清空新消息标识
        List<NewMsgCount_DB> nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(userBdNum)+"");
        if(nMsgList.size() > 0)
        {
            NewMsgCount_DB nMsg = nMsgList.get(0);
            nMsg.NumClear();
            nMsg.save();
            AppBus.getInstance().post(nMsg);
        }
        if(thisContact != null)
        {
            nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(thisContact.getBdNum())+"");
//                NewMsgCount_DB nMsg2 = NewMsgCount_DB.findById(NewMsgCount_DB.class,CommonUtil.Str2long(thisContact.getBdNum()));
            if(nMsgList.size() > 0)
            {
                NewMsgCount_DB nMsg = nMsgList.get(0);
                nMsg.NumClear();
                nMsg.save();
                AppBus.getInstance().post(nMsg);
            }
            nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(thisContact.getPhone())+"");

            if(nMsgList.size() > 0)
            {
                NewMsgCount_DB nMsg = nMsgList.get(0);
                nMsg.NumClear();
                nMsg.save();
                AppBus.getInstance().post(nMsg);
            }
        }
    }

    //listview到底部
    private void scrollMyListViewToBottom() {
        msgLists.post(new Runnable() {
            @Override
            public void run() {            // Select the last row so it will scroll into view...
                msgLists.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }

    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    RefreshUI();
                    break;

                case 1001:
                    editConMsg.append(msgSavedList.get(msg.arg1));
                    break;
            }
        }
    };

    //发送报文方法
    private void SendMsg()
    {
        if(sendMode == 0)
        {
            //北斗发送
            if(posSwitch.isChecked())
            {
                //附带位置
                final Message_DB msgDb = new Message_DB();
                try
                {
//                        msgDb.setRcvAddress(userName);
//                        msgDb.setRcvUserName(userName);
//                        msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
//                        msgDb.setSendUserName(BdCardBean.getInstance().getIdNum());

                    msgDb.setRcvAddress(userBdNum);
                    msgDb.setRcvUserId(Contact_DB.getIdViaAddress(msgDb.getRcvAddress()));
                    msgDb.setRcvUserName(Contact_DB.getNameViaId(msgDb.getRcvUserId(),msgDb.getRcvAddress()));

                    msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
                    msgDb.setSendUserId(Contact_DB.getIdViaAddress(msgDb.getSendAddress()));
                    msgDb.setSendUserName(Contact_DB.getNameViaId(msgDb.getSendUserId(),msgDb.getSendAddress()));

                    msgDb.setMsgTime(System.currentTimeMillis());
                    msgDb.setMsgType(0);
                    msgDb.setMsgSendStatue(0);
                    msgDb.setMsgCon(TextUtils.isEmpty(editConMsg.getText().toString())?
                            getResources().getString(R.string.pos_info_text):editConMsg.getText().toString());
                    msgDb.setMsgPos(DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()),0)
                            +DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat()),0));
                    msgDb.setRemark("");
                    msgDb.setDelFlag(0);

                    long _id = msgDb.save();
                    BdCardBean.getInstance().setMsgSendingId(_id);
                    LogUtil.i(_id+" saved");

                }
                catch(Exception e)
                {
                    LogUtil.e(e.toString());
                }
                String con = "";
                try {
                    con = getResources().getString(R.string.msg_code_start_index)+getResources().getString(R.string.msg_code_pos_msg)+
                            msgDb.getMsgPos()+DataUtil.byte2HexStr(msgDb.getMsgCon().getBytes("GB2312"),0);
                    con = con + DataUtil.byte2HexStr(new byte[]{DataUtil.xor(DataUtil.hexStr2Bytes(con),DataUtil.hexStr2Bytes(con).length)},0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(userBdNum, 1, 1, con), null));

                //超时判断
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(msgDb.getMsgSendStatue() == 0 && System.currentTimeMillis()-msgDb.getMsgTime()<10*1000)
                        {

                        }
                        Message_DB msgTmp = Message_DB.findById(Message_DB.class,msgDb.getId());

                        if(msgTmp.getMsgSendStatue() == 0)
                        {
                            msgTmp.setMsgSendStatue(2);
                            msgTmp.save();
                            mHandler.sendEmptyMessage(1);
                        }

                    }
                }).start();

                editConMsg.setText("");
                textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                textAddInfo.setTextColor(getResources().getColor(R.color.black));
                RefreshUI();

            }
            else
            {
                //无位置
                if(!TextUtils.isEmpty(editConMsg.getText().toString()))
                {
                    final Message_DB msgDb = new Message_DB();
                    try
                    {
//                        msgDb.setRcvAddress(userName);
//                        msgDb.setRcvUserName(userName);
//                        msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
//                        msgDb.setSendUserName(BdCardBean.getInstance().getIdNum());

                        msgDb.setRcvAddress(userBdNum);
                        msgDb.setRcvUserId(Contact_DB.getIdViaAddress(msgDb.getRcvAddress()));
                        msgDb.setRcvUserName(Contact_DB.getNameViaId(msgDb.getRcvUserId(),msgDb.getRcvAddress()));

                        msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
                        msgDb.setSendUserId(Contact_DB.getIdViaAddress(msgDb.getSendAddress()));
                        msgDb.setSendUserName(Contact_DB.getNameViaId(msgDb.getSendUserId(),msgDb.getSendAddress()));

                        msgDb.setMsgTime(System.currentTimeMillis());
                        msgDb.setMsgType(0);
                        msgDb.setMsgSendStatue(0);
                        msgDb.setMsgCon(editConMsg.getText().toString());
                        msgDb.setMsgPos("");
                        msgDb.setRemark("");
                        msgDb.setDelFlag(0);

                        long _id = msgDb.save();
                        BdCardBean.getInstance().setMsgSendingId(_id);
                        LogUtil.i(_id+" saved");

                    }
                    catch(Exception e)
                    {
                        LogUtil.e(e.toString());
                    }
                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(userBdNum, 1, 2, editConMsg.getText().toString()), null));

                    //超时判断
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(msgDb.getMsgSendStatue() == 0 && System.currentTimeMillis()-msgDb.getMsgTime()<10*1000)
                            {

                            }
                            Message_DB msgTmp = Message_DB.findById(Message_DB.class,msgDb.getId());

                            if(msgTmp.getMsgSendStatue() == 0)
                            {
                                msgTmp.setMsgSendStatue(2);
                                msgTmp.save();
                                mHandler.sendEmptyMessage(1);
                            }

                        }
                    }).start();

                    editConMsg.setText("");
                    textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                    textAddInfo.setTextColor(getResources().getColor(R.color.black));
                    RefreshUI();
                }
            }
        }
        else if(sendMode == 1)
        {
            //手机发送
            //无位置
            if(!TextUtils.isEmpty(editConMsg.getText().toString()))
            {
                final Message_DB msgDb = new Message_DB();
                try
                {

                    msgDb.setRcvAddress(userBdNum);
                    msgDb.setRcvUserId(Contact_DB.getIdViaAddress(msgDb.getRcvAddress()));
                    msgDb.setRcvUserName(Contact_DB.getNameViaId(msgDb.getRcvUserId(),msgDb.getRcvAddress()));

                    msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
                    msgDb.setSendUserId(Contact_DB.getIdViaAddress(msgDb.getSendAddress()));
                    msgDb.setSendUserName(Contact_DB.getNameViaId(msgDb.getSendUserId(),msgDb.getSendAddress()));

                    msgDb.setMsgTime(System.currentTimeMillis());
                    msgDb.setMsgType(0);
                    msgDb.setMsgSendStatue(0);
                    msgDb.setMsgCon(editConMsg.getText().toString());
                    msgDb.setMsgPos("");
                    msgDb.setRemark("");
                    msgDb.setDelFlag(0);

                    long _id = msgDb.save();
                    BdCardBean.getInstance().setMsgSendingId(_id);
                    LogUtil.i(_id+" saved");

                }
                catch(Exception e)
                {
                    LogUtil.e(e.toString());
                }

                try {
                    String sendCon = getResources().getString(R.string.msg_code_start_index)+getResources().getString(R.string.msg_code_sms_msg)+
                            DataUtil.BdNum2BytesStr(BdCardBean.getInstance().getIdNum())+DataUtil.PhoneNum2BytesStr(userBdNum)+
                            DataUtil.byte2HexStr(msgDb.getMsgCon().getBytes("GB2312"),0);
                    sendCon = sendCon +
                            DataUtil.byte2HexStr(new byte[]{DataUtil.xor(DataUtil.hexStr2Bytes(sendCon),DataUtil.hexStr2Bytes(sendCon).length)},0);
                    String rcvNum = SettingSharedPreference.getDataString(ChatActivity.this,BD_SERVICE_NUM);
                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(rcvNum, 1, 1, sendCon), null));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(userBdNum, 1, 2, editConMsg.getText().toString()), null));

                //超时判断
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(msgDb.getMsgSendStatue() == 0 && System.currentTimeMillis()-msgDb.getMsgTime()<10*1000)
                        {

                        }
                        Message_DB msgTmp = Message_DB.findById(Message_DB.class,msgDb.getId());

                        if(msgTmp.getMsgSendStatue() == 0)
                        {
                            msgTmp.setMsgSendStatue(2);
                            msgTmp.save();
                            mHandler.sendEmptyMessage(1);
                        }

                    }
                }).start();

                editConMsg.setText("");
                textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                textAddInfo.setTextColor(getResources().getColor(R.color.black));
                RefreshUI();
            }
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnSend:
//                if(BtConnectInfo.getInstance().isConnect() || !TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
//                {
//                    SendMsg();
//                }
//                else
//                {
//                    ShowToast(getResources().getString(R.string.cant_send_msg));
//                }
//
//                break;
//            case R.id.imageAddInput:
//                if(addInputBox.getVisibility() == View.VISIBLE)
//                {
//                    addInputBox.setVisibility(View.GONE);
//                }
//                else if(addInputBox.getVisibility() == View.GONE)
//                {
//                    addInputBox.setVisibility(View.VISIBLE);
//                }
//                break;
            case R.id.imageBack:
                finish();
                break;

            case R.id.textUserName:
                if(thisContact!=null)
                {
                    Intent intent = new Intent(ChatActivity.this, ContractInfoActivity.class);
                    intent.putExtra("contact_id", thisContact.getId());
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(ChatActivity.this, NewContactActivity.class);
                    intent.putExtra("num", userBdNum);
                    startActivity(intent);
                }

                break;
        }
    }

    @Subscribe
    public void setContent(Message_DB data) {
        if(!TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()) && data.getRcvAddress().equals(BdCardBean.getInstance().getIdNum()))
        {
            RefreshUI();
        }

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

        //清空新消息标识
        List<NewMsgCount_DB> nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(userBdNum)+"");
        if(nMsgList.size() > 0)
        {
            NewMsgCount_DB nMsg = nMsgList.get(0);
            nMsg.NumClear();
            nMsg.save();
            AppBus.getInstance().post(nMsg);
        }
        if(thisContact != null)
        {
            nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(thisContact.getBdNum())+"");
//                NewMsgCount_DB nMsg2 = NewMsgCount_DB.findById(NewMsgCount_DB.class,CommonUtil.Str2long(thisContact.getBdNum()));
            if(nMsgList.size() > 0)
            {
                NewMsgCount_DB nMsg = nMsgList.get(0);
                nMsg.NumClear();
                nMsg.save();
                AppBus.getInstance().post(nMsg);
            }
            nMsgList = NewMsgCount_DB.find(NewMsgCount_DB.class, "num_ID = ?",CommonUtil.Str2long(thisContact.getPhone())+"");

            if(nMsgList.size() > 0)
            {
                NewMsgCount_DB nMsg = nMsgList.get(0);
                nMsg.NumClear();
                nMsg.save();
                AppBus.getInstance().post(nMsg);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshUI();
        RefreshUI();
    }
}
