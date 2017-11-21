package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Message_DB;
import com.xyxl.tianyingn3.util.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21 15:25
 * Version : V1.0
 * Introductions :
 */

public class ChatAdapter extends BaseAdapter {
    private List<Message_DB> msgList;
    private LayoutInflater mInflater;
    private Context mContext;

    private long priTime = 0;
    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvPos, tvTime, tvCon, tvSend;
    }
    ViewHolder viewHolder = null;

    public ChatAdapter(Context context, List<Message_DB> datalist) {
        mContext = context;
        this.msgList = datalist;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        Message_DB entity = msgList.get(position);

//        if (convertView == null)
//        {
            viewHolder = new ViewHolder();
            if(entity.getMsgType() == 0)
            {
                convertView = mInflater.inflate(
                        R.layout.chat_send_item, null);
                viewHolder.tvSend = (TextView) convertView
                        .findViewById(R.id.textSendStatus);
            }
            else if(entity.getMsgType() == 1)
            {
                convertView = mInflater.inflate(
                        R.layout.chat_rcv_item, null);

            }

            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.imageChatRcvHead);
            viewHolder.tvPos = (TextView) convertView
                    .findViewById(R.id.textRcvPos);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.textTimeRcv);
            viewHolder.tvCon = (TextView) convertView
                    .findViewById(R.id.textRcv);
            convertView.setTag(viewHolder);

//        }
//        else
//        {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }


        //发送状态
        try {
            if (entity.getMsgType() == 0) {
                if (entity.getMsgSendStatue() == 0) {
                    viewHolder.tvSend.setText(mContext.getResources().getString(R.string.sending));
                } else if (entity.getMsgSendStatue() == 2) {
                    viewHolder.tvSend.setText(mContext.getResources().getString(R.string.send_failed));
                } else {
                    viewHolder.tvSend.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {

        }


        //消息时间
        if(position == 0 || entity.getMsgTime()-msgList.get(position-1).getMsgTime()>120*1000)
        {
            priTime = entity.getMsgTime();
            viewHolder.tvTime.setText(TimeUtil.formatDateTime(new Date(entity.getMsgTime())));
        }
        else
        {
            viewHolder.tvTime.setVisibility(View.GONE);
        }

        //位置信息
        if (TextUtils.isEmpty(entity.getMsgPos())) {
            viewHolder.tvPos.setVisibility(View.GONE);
        } else {
            viewHolder.tvPos.setText(entity.getSendUserName());
        }

        //具体内容
        viewHolder.tvCon.setText(entity.getMsgCon());

        return convertView;
    }
}
