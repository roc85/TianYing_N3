package com.xyxl.tianyingn3.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.customview.ChatAdapter;
import com.xyxl.tianyingn3.ui.customview.DialogView;
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
    private ImageView imageSendType;
    private TextView textUserName;
    private TextView textAddInfo;
    private LinearLayout userInfos;
    private RelativeLayout titleBox;
    private ImageView imageAddInput;
    private Button btnSend;
    private EditText editInput;
    private RelativeLayout textInputBox;
    private Switch switchSendPos;
    private TextView textPos;
    private RelativeLayout addInputBox;
    private LinearLayout inputBox;
    private ListView msgLists;

    //变量
    private String userName, userBdNum;
    private Contact_DB thisContact;
    private List<Message_DB> chatDatas;
    private ChatAdapter chatAdapter;

    private List<String> msgSavedList = new ArrayList<String>();

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
            }
            else if(thisMsg.getMsgType() == 1)
            {
                num = thisMsg.getSendAddress();
            }
            userBdNum = num;
            thisContact = Contact_DB.findById(Contact_DB.class,Contact_DB.getIdViaAddress(userBdNum));

            chatDatas = Message_DB.find(Message_DB.class,
                    "( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?)",
                    new String[]{BdCardBean.getInstance().getIdNum(),num,"0",num,BdCardBean.getInstance().getIdNum() ,"1"},
                    null,"msg_Time",null);
            chatAdapter = new ChatAdapter(ChatActivity.this,chatDatas);

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
        imageSendType = (ImageView) findViewById(R.id.imageSendType);
        textUserName = (TextView) findViewById(R.id.textUserName);
        textAddInfo = (TextView) findViewById(R.id.textAddInfo);
        userInfos = (LinearLayout) findViewById(R.id.userInfos);
        titleBox = (RelativeLayout) findViewById(R.id.titleBox);
        imageAddInput = (ImageView) findViewById(R.id.imageAddInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        editInput = (EditText) findViewById(R.id.editInput);
        textInputBox = (RelativeLayout) findViewById(R.id.textInputBox);
        switchSendPos = (Switch) findViewById(R.id.switchSendPos);
        textPos = (TextView) findViewById(R.id.textPos);
        addInputBox = (RelativeLayout) findViewById(R.id.addInputBox);
        inputBox = (LinearLayout) findViewById(R.id.inputBox);
        msgLists = (ListView) findViewById(R.id.msgLists);

        msgLists.setAdapter(chatAdapter);
        scrollMyListViewToBottom();
        addInputBox.setVisibility(View.GONE);

        btnSend.setOnClickListener(this);
        imageAddInput.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        imageInfo.setOnClickListener(this);

        textPos.setText("Pos:\n"+ MyPosition.getInstance().getMyLon()+"\n"+MyPosition.getInstance().getMyLat());
        textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
        textAddInfo.setTextColor(getResources().getColor(R.color.black));

        //设置用户
        if(thisMsg.getMsgType() == 0)
        {
            userName = thisMsg.getRcvUserName();
        }
        else if(thisMsg.getMsgType() == 1)
        {
            userName = thisMsg.getSendUserName();
        }
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
            imageSendType.setVisibility(View.GONE);
        }

        //位置选择
        switchSendPos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                try {
                    textAddInfo.setText((editInput.getText().toString().getBytes("GB2312").length+(switchSendPos.isChecked()?8:0))+"/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        //根据输入框输入值的改变来过滤搜索
        editInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if(!TextUtils.isEmpty(s.toString()))
                {
                    editInput.setTextColor(getResources().getColor(R.color.white));
                    btnSend.setEnabled(true);
//                    if(switchSendPos.isChecked())
//                    {
//
//                    }
//                    else
//                    {
                    try {
                        textAddInfo.setText((editInput.getText().toString().getBytes("GB2312").length+(switchSendPos.isChecked()?8:0))+"/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(s.toString().getBytes().length>BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()])
                        {
                            editInput.setTextColor(getResources().getColor(R.color.red));
                            btnSend.setEnabled(false);
                            textAddInfo.setTextColor(getResources().getColor(R.color.red));
                        }
                        else
                        {
                            editInput.setTextColor(getResources().getColor(R.color.white));
                            btnSend.setEnabled(true);
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
        editInput.setOnEditorActionListener(new TextView.OnEditorActionListener(){
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
        editInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogView.ShowCHooseDialog(ChatActivity.this,msgSavedList,mHandler);
                return false;
            }
        });

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
        textPos.setText("Pos:\n"+ MyPosition.getInstance().getMyLon()+"\n"+MyPosition.getInstance().getMyLat());

//        chatDatas = Message_DB.find(Message_DB.class,"send_Address = ? AND rcv_Address = ?",
//                new String[]{BdCardBean.getInstance().getIdNum(),thisMsg.getRcvAddress()},null,"msg_Time",null);

        chatDatas = Message_DB.find(Message_DB.class,
                "( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?)",
                new String[]{BdCardBean.getInstance().getIdNum(),userBdNum,"0",userBdNum,BdCardBean.getInstance().getIdNum() ,"1"},
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
            imageSendType.setVisibility(View.GONE);
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
                    editInput.append(msgSavedList.get(msg.arg1));
                    break;
            }
        }
    };

    //发送报文方法
    private void SendMsg()
    {
        if(switchSendPos.isChecked())
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
                msgDb.setMsgCon(editInput.getText().toString());
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
                con = msgDb.getMsgPos()+DataUtil.byte2HexStr(msgDb.getMsgCon().getBytes("GB2312"),0);
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

            editInput.setText("");
            textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
            textAddInfo.setTextColor(getResources().getColor(R.color.black));
            RefreshUI();

        }
        else
        {
            //无位置
            if(!TextUtils.isEmpty(editInput.getText().toString()))
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
                    msgDb.setMsgCon(editInput.getText().toString());
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
                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(userBdNum, 1, 2, editInput.getText().toString()), null));

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

                editInput.setText("");
                textAddInfo.setText("0/"+BD_BYTES_LENS[BdCardBean.getInstance().getCardLv()]);
                textAddInfo.setTextColor(getResources().getColor(R.color.black));
                RefreshUI();
            }
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if(BtConnectInfo.getInstance().isConnect() || !TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
                {
                    SendMsg();
                }
                else
                {
                    ShowToast(getResources().getString(R.string.cant_send_msg));
                }

                break;
            case R.id.imageAddInput:
                if(addInputBox.getVisibility() == View.VISIBLE)
                {
                    addInputBox.setVisibility(View.GONE);
                }
                else if(addInputBox.getVisibility() == View.GONE)
                {
                    addInputBox.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.imageBack:
                finish();
                break;

            case R.id.imageInfo:
                if(thisContact!=null)
                {
                    Intent intent = new Intent(ChatActivity.this, ContractInfoActivity.class);
                    intent.putExtra("contact", thisContact);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshUI();
        RefreshUI();
    }
}
