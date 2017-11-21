package com.xyxl.tianyingn3.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.ui.customview.ChatAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:46
 * Version : V1.0
 * Introductions : 具体消息页面
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private Message_DB thisMsg;
    private ImageView imageInfo;
    private ImageView imageBack;
    private ImageView imageHead;
    private TextView textUserName;
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
    private String userName;
    private List<Message_DB> chatDatas;
    private ChatAdapter chatAdapter;

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
            chatDatas = Message_DB.find(Message_DB.class,"send_Address = ? AND rcv_Address = ?",
                    new String[]{BdCardBean.getInstance().getIdNum(),thisMsg.getRcvAddress()},null,"msg_Time",null);
            chatAdapter = new ChatAdapter(ChatActivity.this,chatDatas);

        } catch (Exception e) {
            LogUtil.e(e.toString());
            finish();
        }
    }

    private void initView() {
        imageInfo = (ImageView) findViewById(R.id.imageInfo);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageHead = (ImageView) findViewById(R.id.imageHead);
        textUserName = (TextView) findViewById(R.id.textUserName);
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

        textPos.setText("Pos:\n"+ MyPosition.getInstance().getMyLon()+"\n"+MyPosition.getInstance().getMyLat());

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
    }

    private void RefreshUI()
    {
        textPos.setText("Pos:\n"+ MyPosition.getInstance().getMyLon()+"\n"+MyPosition.getInstance().getMyLat());

        chatDatas = Message_DB.find(Message_DB.class,"send_Address = ? AND rcv_Address = ?",
                new String[]{BdCardBean.getInstance().getIdNum(),thisMsg.getRcvAddress()},null,"msg_Time",null);
        chatAdapter = new ChatAdapter(ChatActivity.this,chatDatas);
        chatAdapter.notifyDataSetChanged();
        msgLists.setAdapter(chatAdapter);
        scrollMyListViewToBottom();
//        msgLists.scrollTo(msgLists.getScrollX(),msgLists.getHeight());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if(!TextUtils.isEmpty(editInput.getText().toString()))
                {
                    Message_DB msgDb = new Message_DB();
                    try
                    {
                        msgDb.setRcvAddress(userName);
                        msgDb.setRcvUserName(userName);
                        msgDb.setSendAddress(BdCardBean.getInstance().getIdNum());
                        msgDb.setSendUserName(BdCardBean.getInstance().getIdNum());
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
                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA(userName, 1, 2, editInput.getText().toString()), null));

                    editInput.setText("");
                    RefreshUI();
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
}
