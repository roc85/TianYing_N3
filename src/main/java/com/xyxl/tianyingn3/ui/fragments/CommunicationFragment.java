package com.xyxl.tianyingn3.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bluetooth.BTDeviceInfos;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.customview.TotalMsgAdapter;
import com.xyxl.tianyingn3.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 16:01
 * Version : V1.0
 * Introductions :
 */

public class CommunicationFragment extends Fragment implements FinalDatas {

    //UI控件
    private TextView tvNoMsg;
    private ListView totalMsgList;
    private FloatingActionButton newMsg;

    //
    private List<Message_DB> totalMsgDatas = new ArrayList<Message_DB>();
    private TotalMsgAdapter totalMsgAdapter;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:

                    break;

                case 101:

                    break;
                default:
                    break;
            }
        };
    };

    private void ShowToast(String con)
    {
        Toast.makeText(getActivity(),con,Toast.LENGTH_SHORT).show();

    }

    private void initView(View view) {
        tvNoMsg = (TextView)view.findViewById(R.id.textNoMsg);
        totalMsgList = (ListView)view.findViewById(R.id.msgTotalList);
        newMsg = (FloatingActionButton)view.findViewById(R.id.floatingNewMsg);

        newMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BtConnectInfo.getInstance().isConnect() || !TextUtils.isEmpty(BdCardBean.getInstance().getIdNum()))
                {
                    Intent intent = new Intent(getActivity(), NewMsgActivity.class);
                    startActivity(intent);
                }
                else
                {
                    ShowToast(getResources().getString(R.string.cant_send_msg));
                }

            }
        });

        if(totalMsgDatas.size() == 0)
        {
            tvNoMsg.setVisibility(View.VISIBLE);
        }
        else if(totalMsgDatas.size() > 0)
        {
            tvNoMsg.setVisibility(View.GONE);
        }

        totalMsgList.setAdapter(totalMsgAdapter);
        totalMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ChatActivity.class);
                intent.putExtra("msg",totalMsgDatas.get(position));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        try
        {
            String myCardNum = BdCardBean.getInstance().getIdNum();
            if(TextUtils.isEmpty(myCardNum))
            {
                myCardNum = "";
            }
            else
            {

            }
            List<Message_DB> msgDatas = Message_DB.find(Message_DB.class,"send_Address = ? OR rcv_Address = ?",
                    new String[]{myCardNum, myCardNum},
                    null,"msg_Time desc",null);

            List<String> nameList = new ArrayList<String>();
            totalMsgDatas.clear();
            for(int i=0;i<msgDatas.size();i++)
            {
                String name = "";
                if(msgDatas.get(i).getMsgType() == 0)
                {
                    name = msgDatas.get(i).getRcvUserName();
                }
                else if(msgDatas.get(i).getMsgType() == 1)
                {
                    name = msgDatas.get(i).getSendUserName();
                }

                if(!nameList.contains(name))
                {
                    nameList.add(name);
                    totalMsgDatas.add(msgDatas.get(i));
                }
            }
        }
        catch(Error e)
        {
            LogUtil.e(e.toString());
            totalMsgDatas = new ArrayList<Message_DB>();
        }

        totalMsgAdapter = new TotalMsgAdapter(getActivity(),totalMsgDatas);
        totalMsgAdapter.notifyDataSetChanged();
    }

//    @Override
//    protected void initView(View view, Bundle savedInstanceState) {
//        initData();
//
//        initView();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_communication;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_communication, container, false);

        initData();

        initView(view);

        return view;
    }

//    @Override
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        // TODO Auto-generated method stub
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//    }
//
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        initData();
//        LogUtil.i("comm rcv");
//        CommonUtil.ShowToast(getActivity(),"comm rcv" );
        totalMsgList.setAdapter(totalMsgAdapter);

        if(totalMsgDatas.size() == 0)
        {
            tvNoMsg.setVisibility(View.VISIBLE);
        }
        else if(totalMsgDatas.size() > 0)
        {
            tvNoMsg.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Subscribe
    public void setContent(Message_DB data) {
        initData();
        LogUtil.i("comm rcv");
        CommonUtil.ShowToast(getActivity(),"comm rcv" );
        if(totalMsgDatas.size() == 0)
        {
            tvNoMsg.setVisibility(View.VISIBLE);
        }
        else if(totalMsgDatas.size() > 0)
        {
            tvNoMsg.setVisibility(View.GONE);
        }
        totalMsgList.setAdapter(totalMsgAdapter);
    }

    @Subscribe
    public void setContent(BdCardBean data) {
        initData();
        totalMsgList.setAdapter(totalMsgAdapter);

        if(totalMsgDatas.size() == 0)
        {
            tvNoMsg.setVisibility(View.VISIBLE);
        }
        else if(totalMsgDatas.size() > 0)
        {
            tvNoMsg.setVisibility(View.GONE);
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
