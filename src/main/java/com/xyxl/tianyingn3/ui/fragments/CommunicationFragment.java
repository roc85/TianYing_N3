package com.xyxl.tianyingn3.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.bean.BdCardBean;
import com.xyxl.tianyingn3.bean.BtConnectInfo;
import com.xyxl.tianyingn3.bluetooth.BTDeviceInfos;
import com.xyxl.tianyingn3.database.Contact_DB;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.global.App;
import com.xyxl.tianyingn3.global.AppBus;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.logs.LogUtil;
import com.xyxl.tianyingn3.ui.activities.ChatActivity;
import com.xyxl.tianyingn3.ui.activities.NewMsgActivity;
import com.xyxl.tianyingn3.ui.customview.TotalMsgAdapter;
import com.xyxl.tianyingn3.util.CommonUtil;

import java.io.PipedInputStream;
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

    private int choosePosition;

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
        newMsg.setVisibility(View.GONE);
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

        totalMsgList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_confirm, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.textTitle);
        tvTitle.setText("是否删除该会话");
        final Button btnOk = (Button) view.findViewById(R.id.buttonOk);
        final Button btnEsc = (Button) view.findViewById(R.id.buttonEsc);

        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = "";
                if (totalMsgDatas.get(choosePosition).getMsgType() == 0) {
                    num = totalMsgDatas.get(choosePosition).getRcvAddress();
                } else if (totalMsgDatas.get(choosePosition).getMsgType() == 1) {
                    num = totalMsgDatas.get(choosePosition).getSendAddress();
                }

                List<Message_DB> chatDatas = Message_DB.find(Message_DB.class,
                        "del_Flag = ? AND " +
                                "(( send_Address = ? AND rcv_Address = ? AND msg_Type = ? ) OR (send_Address = ? AND rcv_Address = ? AND msg_Type = ?))",
                        new String[]{"0",BdCardBean.getInstance().getIdNum(),num,"0",num,BdCardBean.getInstance().getIdNum() ,"1"},
                        null,"msg_Time",null);
                for(int i=0;i<chatDatas.size();i++)
                {
                    Message_DB msg = chatDatas.get(i);
                    msg.setDelFlag(1);
                    msg.save();
                }

                initData();

                if(totalMsgDatas.size() == 0)
                {
                    tvNoMsg.setVisibility(View.VISIBLE);
                }
                else if(totalMsgDatas.size() > 0)
                {
                    tvNoMsg.setVisibility(View.GONE);
                }
                totalMsgList.setAdapter(totalMsgAdapter);
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
            List<Message_DB> msgDatas = Message_DB.find(Message_DB.class,
                    "del_Flag = ? AND ( send_Address = ? OR rcv_Address = ?)",
                    new String[]{"0",myCardNum, myCardNum},
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
//        CommonUtil.ShowToast(getActivity(),"comm rcv" );
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
