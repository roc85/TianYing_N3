package com.xyxl.tianyingn3.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.customview.TotalMsgAdapter;

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
    private List<Message_DB> totalMsgDatas;
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
                ShowToast("New Msg");
                Intent intent = new Intent(getActivity(), NewMsgActivity.class);
                startActivity(intent);
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
    }

    private void initData() {
        try
        {
            Message_DB msgDb = new Message_DB();
            msgDb.setMsgId(1);
            msgDb.setRcvAddress("");
            msgDb.setRcvUserName("22");
            msgDb.setMsgType(0);
            msgDb.setMsgCon("123");
            msgDb.setMsgTime(System.currentTimeMillis());
            msgDb.setSendUserName("11");
            msgDb.save();

//            totalMsgDatas = Message_DB.find(Message_DB.class,"sendAddress = ? OR rcvAddress = ?",
//                    new String[]{BdCardBean.getInstance().getIdNum(),BdCardBean.getInstance().getIdNum()},
//                    null,"msgTime desc",null);
            totalMsgDatas = Message_DB.listAll(Message_DB.class);
        }
        catch(Error e)
        {
            LogUtil.e(e.toString());
            totalMsgDatas = new ArrayList<Message_DB>();
        }

        totalMsgAdapter = new TotalMsgAdapter(getActivity(),totalMsgDatas);
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

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Subscribe
    public void setContent(Message_DB data) {
        initData();
        totalMsgList.setAdapter(totalMsgAdapter);
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
