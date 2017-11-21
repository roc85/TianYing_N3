package com.xyxl.tianyingn3.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.MessageBean;
import com.xyxl.tianyingn3.bean.MyPosition;
import com.xyxl.tianyingn3.bluetooth.BtSendDatas;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.TestMsg;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.solutions.BdSdk_v2_1;
import com.xyxl.tianyingn3.ui.activities.BluetoothActivity;

/**
 * Created by Administrator on 2017/11/13 16:58
 * Version : V1.0
 * Introductions : 设置功能页面
 */

public class SettingFragment extends BaseFragment {
    private TextView textResult;
    private Button btnTest, btnBt;
    private TextView textInfos;
    private Chronometer timer;

    private boolean isTesting = false;

    private RelativeLayout btSetBox;
    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        textResult = (TextView)view.findViewById(R.id.textView5);
        textInfos = (TextView)view.findViewById(R.id.textInfos);
        btnTest = (Button)view.findViewById(R.id.button);
        btnBt = (Button)view.findViewById(R.id.button2);

        timer = (Chronometer)view.findViewById(R.id.chronometer2);
        timer.start();
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                textResult.setText("id:"+BdCardBean.getInstance().getIdNum()+"\n"+TestMsg.getInstance().toString());
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendICA(0, 0),null));
                if(btnTest.getText().equals("发送报文"))
                {
                    btnTest.setText("测试中");
                    TestMsg.getInstance().ClearDatas();
                    textResult.setText(TestMsg.getInstance().toString());
                    isTesting = true;
                    mHandler.sendEmptyMessage(1);
//                    AppBus.getInstance().post(new BtSendDatas(0, BdSdk_v2_1.BD_SendTXA("306631", 1, 2, TestMsg.TEST_MSG), null));
                }
                else
                {
                    btnTest.setText("发送报文");
                    isTesting = false;
                }
            }
        });

        btnBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
            }
        });

        btSetBox = (RelativeLayout)view.findViewById(R.id.btSetBox);
        btSetBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHoldingActivity().OpenActivity(false, BluetoothActivity.class);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set;
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
        textResult.setText(TestMsg.getInstance().toString());
    }

    @Subscribe
    public void setContent(BdCardBean data) {

        textInfos.setText(BdCardBean.getInstance().toString()+"\nPos:"+ MyPosition.getInstance().getMyLon()+" "+MyPosition.getInstance().getMyLat());
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
