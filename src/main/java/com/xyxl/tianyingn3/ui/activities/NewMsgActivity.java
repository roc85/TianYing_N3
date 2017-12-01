package com.xyxl.tianyingn3.ui.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.SettingSharedPreference;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.solutions.GpsData;
import com.xyxl.tianyingn3.ui.customview.ContactFindAdapter;
import com.xyxl.tianyingn3.ui.customview.DialogView;
import com.xyxl.tianyingn3.util.DataUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 新建报文页面
 */
public class NewMsgActivity extends BaseActivity {


    private ImageView imageDeviceSend;
    private ImageView imagePhoneSend;
    private ImageView imageAddInputSend;
    private EditText editConMsg;
    private RelativeLayout inputConBox;
    private TextView textSet;
    private TextView textTitle;
    private RelativeLayout newMsgTitle;
    private ImageView imageSearhContact;
    private TextView textContactFlag;
    private TextView textPosInfos;
    private EditText editContact;
    private RelativeLayout inputContactBox;
    private RelativeLayout addBox;
    private ListView findContactList;
    private Switch posSwitch;
    private Chronometer timer;
    private TextView textAddInfo;

    private Contact_DB chooseContact;
    private List<Contact_DB> contactList;
    private ContactFindAdapter contactFindAdapter;

    private int textmax;
    //发送模式 0-设备 1-手机
    private int sendMode = 0;
    //附带位置标识
    private int posSend = 0;
    //接收方地址
    private String rcvNum, sendCon;

    private List<String> msgSavedList = new ArrayList<String>();

    private List<Contact_DB> cList;

    //选择列表类型 1-联系人 2-预制报文
    private int chooseType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitFlag(0);
        setContentView(R.layout.activity_new_msg);

        cList = Contact_DB.listAll(Contact_DB.class);

        msgSavedList.clear();
        for(int i = 0;i<SAVED_MSG_MAX;i++)
        {
            if(!TextUtils.isEmpty(SettingSharedPreference.getDataString(this,SAVED_MSG_INFO+i)))
            {
                msgSavedList.add(SettingSharedPreference.getDataString(this,SAVED_MSG_INFO+i));
            }
        }

        initView();
    }


    private void initView() {
        imageDeviceSend = (ImageView) findViewById(R.id.imageDeviceSend);
        imagePhoneSend = (ImageView) findViewById(R.id.imagePhoneSend);
        imageAddInputSend = (ImageView) findViewById(R.id.imageAddInputSend);
        editConMsg = (EditText) findViewById(R.id.editConMsg);
        inputConBox = (RelativeLayout) findViewById(R.id.inputConBox);
        textSet = (TextView) findViewById(R.id.textSet);
        textTitle = (TextView) findViewById(R.id.textTitle);
        newMsgTitle = (RelativeLayout) findViewById(R.id.newMsgTitle);
        imageSearhContact = (ImageView) findViewById(R.id.imageSearhContact);
        textContactFlag = (TextView) findViewById(R.id.textContactFlag);
        textPosInfos = (TextView) findViewById(R.id.textPosInfo);
        editContact = (EditText) findViewById(R.id.editContact);
        inputContactBox = (RelativeLayout) findViewById(R.id.inputContactBox);
        addBox = (RelativeLayout) findViewById(R.id.addSendBox);
        findContactList = (ListView) findViewById(R.id.findContactList);
        posSwitch=(Switch)findViewById(R.id.switchPosSend);
        textAddInfo = (TextView) findViewById(R.id.textAddInfo);
        timer=(Chronometer)findViewById(R.id.newMsgTimer);
        timer.start();
        initUI();

        initListener();
    }

    // Hander
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change

                    break;

                case 1001:
                    if(chooseType == 1)
                    {
                        chooseContact = cList.get(msg.arg1);
                        if(sendMode == 0)
                        {
                            editContact.setText(chooseContact.getBdNum());
                        }
                        else if(sendMode == 1)
                        {
                            editContact.setText(chooseContact.getPhone());
                        }
                    }
                    else if(chooseType == 2)
                    {
                        editConMsg.append(msgSavedList.get(msg.arg1));
                    }
                    chooseType = 0;
                    break;
                case 1002:
                    AppBus.getInstance().post((Message_DB)msg.obj);
                    break;
            }
        }
    };

    private void initListener() {
        textSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageSearhContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> users = new ArrayList<>();
                for(int i = 0;i<cList.size();i++)
                {
                    users.add(cList.get(i).getContactName()+" "+cList.get(i).getBdNum()+ " " +cList.get(i).getPhone());
                }
                chooseType = 1;
                DialogView.ShowChooseDialog(NewMsgActivity.this,users,mHandler);
            }
        });

        imageDeviceSend.setSelected(true);
        imageDeviceSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDeviceSend.setSelected(true);
                imagePhoneSend.setSelected(false);
                sendMode = 0;
                editConMsg.setHint(getResources().getString(R.string.send_to_device));
                if(chooseContact!=null)
                {
                    if(sendMode == 0)
                    {
                        editContact.setText(chooseContact.getBdNum());
                    }
                    else if(sendMode == 1)
                    {
                        editContact.setText(chooseContact.getPhone());
                    }
                }


            }
        });

        imagePhoneSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDeviceSend.setSelected(false);
                imagePhoneSend.setSelected(true);
                sendMode = 1;
                editConMsg.setHint(getResources().getString(R.string.send_to_phone));
                posSwitch.setChecked(false);
                posSend = 0;
                if(chooseContact!=null)
                {
                    if(sendMode == 0)
                    {
                        editContact.setText(chooseContact.getBdNum());
                    }
                    else if(sendMode == 1)
                    {
                        editContact.setText(chooseContact.getPhone());
                    }
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

                    submit();
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

        editContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString()))
                {
                    filterData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseContact = contactList.get(position);
                if(sendMode == 0)
                {
                    editContact.setText(chooseContact.getBdNum());
                }
                else if(sendMode == 1)
                {
                    editContact.setText(chooseContact.getPhone());
                }

            }
        });

        //输入框长按
        editConMsg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                chooseType = 2;
                DialogView.ShowChooseDialog(NewMsgActivity.this,msgSavedList,mHandler);
                return false;
            }
        });
    }



    private void filterData(String s) {
        contactList = Contact_DB.find(Contact_DB.class,"phone LIKE ? OR bd_Num LIKE ?","%"+s+"%","%"+s+"%");
        contactFindAdapter = new ContactFindAdapter(NewMsgActivity.this,contactList,s);
        findContactList.setAdapter(contactFindAdapter);
    }

    //发送报文
    private void SendMsg() {
        final Message_DB msgDb = new Message_DB();
        try {

            msgDb.setRcvAddress(BdCardBean.FormatCardNum(rcvNum));
            msgDb.setRcvUserId(Contact_DB.getIdViaAddress(msgDb.getRcvAddress()));
            msgDb.setRcvUserName(Contact_DB.getNameViaId(msgDb.getRcvUserId(), msgDb.getRcvAddress()));

            msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
            msgDb.setSendUserId(Contact_DB.getIdViaAddress(msgDb.getSendAddress()));
            msgDb.setSendUserName(Contact_DB.getNameViaId(msgDb.getSendUserId(), msgDb.getSendAddress()));

            msgDb.setMsgTime(System.currentTimeMillis());
            msgDb.setMsgType(0);
            msgDb.setMsgSendStatue(0);
            msgDb.setMsgCon(sendCon);
            if(posSend == 1)
            {
                msgDb.setMsgPos(DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLon()), 0)
                        + DataUtil.byte2HexStr(GpsData.GetPosDataBytes(MyPosition.getInstance().getMyLat()), 0));
            }
            else if(posSend == 0)
            {
                msgDb.setMsgPos("");
            }

            msgDb.setRemark("");
            msgDb.setDelFlag(0);

            long _id = msgDb.save();
            BdCardBean.getInstance().setMsgSendingId(_id);
            LogUtil.i(_id + " saved");

        } catch (Exception e) {
            LogUtil.e(e.toString());
        }

        if(posSend == 0)
        {
            //无位置
            if(sendMode == 0)
            {
                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(rcvNum, 1, 2, sendCon), null));
            }
            else if(sendMode == 1)
            {
                try {
                    sendCon = getResources().getString(R.string.msg_code_start_index)+getResources().getString(R.string.msg_code_sms_msg)+
                            DataUtil.BdNum2BytesStr(BdCardBean.getInstance().getIdNum())+DataUtil.PhoneNum2BytesStr(rcvNum)+
                            DataUtil.byte2HexStr(msgDb.getMsgCon().getBytes("GB2312"),0);
                    sendCon = sendCon +
                            DataUtil.byte2HexStr(new byte[]{DataUtil.xor(DataUtil.hexStr2Bytes(sendCon),DataUtil.hexStr2Bytes(sendCon).length)},0);
                    rcvNum = SettingSharedPreference.getDataString(NewMsgActivity.this,BD_SERVICE_NUM);
                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(rcvNum, 1, 1, sendCon), null));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
        else if(posSend == 1)
        {
            //带位置
            String con = "";
            try {
                con = getResources().getString(R.string.msg_code_start_index)+getResources().getString(R.string.msg_code_pos_msg)+
                        msgDb.getMsgPos()+DataUtil.byte2HexStr(msgDb.getMsgCon().getBytes("GB2312"),0);
                con = con +
                        DataUtil.byte2HexStr(new byte[]{DataUtil.xor(DataUtil.hexStr2Bytes(con),DataUtil.hexStr2Bytes(con).length)},0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            }
            AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(rcvNum, 1, 1, con), null));
        }

        AppBus.getInstance().post(msgDb);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (msgDb.getMsgSendStatue() == 0 && System.currentTimeMillis() - msgDb.getMsgTime() < 10 * 1000) {
//                            LogUtil.e("sending_wait");
                }
                Message_DB msgTmp = Message_DB.findById(Message_DB.class, msgDb.getId());

                if (msgTmp.getMsgSendStatue() == 0) {
                    msgTmp.setMsgSendStatue(2);
                    msgTmp.save();
                    Message msg = new Message();
                    msg.what = 1002;
                    msg.obj = msgTmp;
                    mHandler.sendMessage(msg);

                }

            }
        }).start();

        finish();
    }

    private void initUI() {

        RelativeLayout.LayoutParams relaParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMyDpHeight(0));
        relaParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addBox.setLayoutParams(relaParams);

    }

    private void submit() {
        // validate
        String editConMsgString = editConMsg.getText().toString().trim();
        if (posSend == 0 && TextUtils.isEmpty(editConMsgString)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String editContactString = editContact.getText().toString().trim();
        if (TextUtils.isEmpty(editContactString)) {
            Toast.makeText(this, "接收方地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        rcvNum = editContact.getText().toString().trim();
        sendCon = editConMsg.getText().toString();
        SendMsg();


    }
}

//
//case R.id.button3:
//final Message_DB msgDb = new Message_DB();
//        try
//        {
//
//        msgDb.setRcvAddress(BdCardBean.FormatCardNum(editText.getText().toString()));
//        msgDb.setRcvUserId(Contact_DB.getIdViaAddress(msgDb.getRcvAddress()));
//        msgDb.setRcvUserName(Contact_DB.getNameViaId(msgDb.getRcvUserId(),msgDb.getRcvAddress()));
//
//        msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
//        msgDb.setSendUserId(Contact_DB.getIdViaAddress(msgDb.getSendAddress()));
//        msgDb.setSendUserName(Contact_DB.getNameViaId(msgDb.getSendUserId(),msgDb.getSendAddress()));
//
//        msgDb.setMsgTime(System.currentTimeMillis());
//        msgDb.setMsgType(0);
//        msgDb.setMsgSendStatue(0);
//        msgDb.setMsgCon(editText2.getText().toString());
//        msgDb.setMsgPos("");
//        msgDb.setRemark("");
//        msgDb.setDelFlag(0);
//
//        long _id = msgDb.save();
//        BdCardBean.getInstance().setMsgSendingId(_id);
//        LogUtil.i(_id+" saved");
//
//        }
//        catch(Exception e)
//        {
//        LogUtil.e(e.toString());
//        }
//        AppBus.getInstance().post(msgDb);
//        AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(editText.getText().toString(), 1, 2, editText2.getText().toString()), null));
//
//        new Thread(new Runnable() {
//@Override
//public void run() {
//        while(msgDb.getMsgSendStatue() == 0 && System.currentTimeMillis()-msgDb.getMsgTime()<10*1000)
//        {
////                            LogUtil.e("sending_wait");
//        }
//        Message_DB msgTmp = Message_DB.findById(Message_DB.class,msgDb.getId());
//
//        if(msgTmp.getMsgSendStatue() == 0)
//        {
//        msgTmp.setMsgSendStatue(2);
//        msgTmp.save();
//
//        }
//
//        }
//        }).start();
//        break;