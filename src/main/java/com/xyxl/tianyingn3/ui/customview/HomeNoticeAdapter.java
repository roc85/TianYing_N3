package com.xyxl.tianyingn3.ui.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyxl.tianyingn3.R;
import com.xyxl.tianyingn3.database.Notice_DB;
import com.xyxl.tianyingn3.global.FinalDatas;
import com.xyxl.tianyingn3.util.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21 9:54
 * Version : V1.0
 * Introductions :
 */

public class HomeNoticeAdapter extends BaseAdapter implements FinalDatas {

    private List<Notice_DB> noticeList;
    private LayoutInflater mInflater;
    private Context mContext;
    static class ViewHolder
    {
        public ImageView imgHead;
        public TextView tvType, tvTime, tvCon;
    }
    ViewHolder viewHolder = null;

    public HomeNoticeAdapter(Context context, List<Notice_DB> datalist) {
        mContext = context;
        this.noticeList = datalist;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notice_DB entity = noticeList.get(position);

//        if (convertView == null)
//        {
            convertView = mInflater.inflate(
                    R.layout.notice_home_item, null);

            viewHolder = new ViewHolder();
//            viewHolder.imgHead = (ImageView) convertView
//                    .findViewById(R.id.tmHead);
            viewHolder.tvType = (TextView) convertView
                    .findViewById(R.id.textType);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.textTimeRcv);
            viewHolder.tvCon = (TextView) convertView
                    .findViewById(R.id.textCon);
            convertView.setTag(viewHolder);
//        }
//        else
//        {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }

        if(entity.getNoticeType() == 0)
        {
            viewHolder.tvType.setText("新消息");

        }
        else if(entity.getNoticeType() == 1)
        {
            viewHolder.tvType.setText("设备信息");

        }

        viewHolder.tvTime.setText(TimeUtil.formatFriendly(new Date(entity.getNoticeTime())));

        viewHolder.tvCon.setText(entity.getNoticeCon());

        return convertView;
    }
}
