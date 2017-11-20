package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
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
 * Created by Administrator on 2017/11/17 10:47
 * Version : V1.0
 * Introductions : 整体消息列表适配器
 */

public class TotalMsgAdapter extends BaseAdapter {

    private List<Message_DB> msgList;
    private LayoutInflater mInflater;
    private Context mContext;
    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvName, tvTime, tvCon;
    }
    ViewHolder viewHolder = null;

    public TotalMsgAdapter(Context context, List<Message_DB> datalist) {
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
//        return msgList.get(position).getMsgId();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message_DB entity = msgList.get(position);

        if (convertView == null)
        {
            convertView = mInflater.inflate(
                    R.layout.total_msg_item, null);

            viewHolder = new ViewHolder();
            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.tmHead);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.tmName);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tmTime);
            viewHolder.tvCon = (TextView) convertView
                    .findViewById(R.id.tmCon);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if(entity.getMsgType() == 0)
//        {
//            viewHolder.tvName.setText(entity.getRcvUserName());
//
//        }
//        else if(entity.getMsgType() == 1)
//        {
//            viewHolder.tvName.setText(entity.getSendUserName());
//
//        }
//
//        viewHolder.tvTime.setText(TimeUtil.formatFriendly(new Date(entity.getMsgTime())));
//
//        viewHolder.tvCon.setText(entity.getMsgCon().length() > 10?(entity.getMsgCon().substring(0,10)+"..."):entity.getMsgCon());

        return convertView;
    }
}
